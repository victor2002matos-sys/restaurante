package br.com.restaurante;

import br.com.restaurante.domain.Pedido;
import br.com.restaurante.domain.StatusPedido;
import br.com.restaurante.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Testa as regras de negócio e casos de uso usando os dados de exemplo. */
@SpringBootTest
@Transactional
class RegrasDeNegocioTest {

    // Dados criados por DadosIniciais
    private static final String GARCOM = "22222222222";
    private static final String CLIENTE = "44444444444";

    @Autowired PedidoService pedidoService;
    @Autowired AvaliacaoService avaliacaoService;

    private NovoPedidoForm pedido(int mesa, String cpf, long pratoId, int quantidade) {
        NovoPedidoForm form = new NovoPedidoForm();
        form.setMesaNumero(mesa);
        form.setClienteCpf(cpf);
        form.getQuantidades().put(pratoId, quantidade);
        return form;
    }

    @Test
    void registraPedidoComStatusAguardandoPreparoECalculaTotal() {
        Pedido p = pedidoService.registrar(pedido(1, CLIENTE, 1L, 2), GARCOM);
        assertThat(p.getStatus()).isEqualTo(StatusPedido.AGUARDANDO_PREPARO);
        assertThat(p.getFuncionario().getCpf()).isEqualTo(GARCOM);
        assertThat(p.getTotal()).isEqualByComparingTo(new BigDecimal("116.00"));
    }

    @Test
    void rn001ClienteNaoCadastradoNaoFazPedido() {
        assertThatThrownBy(() -> pedidoService.registrar(pedido(1, "99999999999", 1L, 1), GARCOM))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("não está cadastrado");
    }

    @Test
    void uc1MesaComPedidoAbertoFicaBloqueada() {
        // A mesa 2 já tem um pedido aguardando preparo nos dados de exemplo
        assertThatThrownBy(() -> pedidoService.registrar(pedido(2, CLIENTE, 1L, 1), GARCOM))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("já possui um pedido");
    }

    @Test
    void pedidoSemPratosNaoEhAceito() {
        assertThatThrownBy(() -> pedidoService.registrar(pedido(1, CLIENTE, 1L, 0), GARCOM))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void statusSegueOFluxoAteFinalizado() {
        Pedido p = pedidoService.registrar(pedido(1, CLIENTE, 1L, 1), GARCOM);
        for (int i = 0; i < 4; i++) {
            pedidoService.avancarStatus(p.getId());
        }
        assertThat(p.getStatus()).isEqualTo(StatusPedido.FINALIZADO);
        assertThatThrownBy(() -> pedidoService.avancarStatus(p.getId())).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void uc3SoAvaliaPedidoFinalizado() {
        Pedido p = pedidoService.registrar(pedido(1, CLIENTE, 1L, 1), GARCOM);
        assertThatThrownBy(() -> avaliacaoService.avaliar(p.getId(), CLIENTE, 9, null))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("finalizados");
    }

    @Test
    void rn004NotaForaDe1a10EhRecusada() {
        Pedido p = pedidoService.registrar(pedido(1, CLIENTE, 1L, 1), GARCOM);
        for (int i = 0; i < 4; i++) {
            pedidoService.avancarStatus(p.getId());
        }
        assertThatThrownBy(() -> avaliacaoService.avaliar(p.getId(), CLIENTE, 11, null))
                .hasMessageContaining("entre 1 e 10");
        assertThatThrownBy(() -> avaliacaoService.avaliar(p.getId(), CLIENTE, 0, null))
                .hasMessageContaining("entre 1 e 10");
        assertThat(avaliacaoService.avaliar(p.getId(), CLIENTE, 10, "Ótimo").getNota()).isEqualTo(10);
    }
}
