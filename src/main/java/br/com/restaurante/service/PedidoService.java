package br.com.restaurante.service;

import br.com.restaurante.domain.*;
import br.com.restaurante.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/** Registro e acompanhamento de pedidos (RF010–RF012, UC-1). */
@Service
@Transactional
public class PedidoService {

    private static final Set<StatusPedido> ENCERRADOS = EnumSet.of(StatusPedido.FINALIZADO, StatusPedido.CANCELADO);

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PratoRepository pratoRepository;
    private final PrecoService precoService;

    public PedidoService(PedidoRepository pedidoRepository, MesaRepository mesaRepository,
                         FuncionarioRepository funcionarioRepository,
                         PratoRepository pratoRepository, PrecoService precoService) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.pratoRepository = pratoRepository;
        this.precoService = precoService;
    }

    public boolean mesaOcupada(Mesa mesa) {
        return pedidoRepository.existsByMesaAndStatusNotIn(mesa, ENCERRADOS);
    }

    public Pedido registrar(NovoPedidoForm form, String funcionarioCpf) {
        if (form.getMesaNumero() == null) {
            throw new RegraNegocioException("Selecione a mesa.");
        }
        Mesa mesa = mesaRepository.findById(form.getMesaNumero())
                .orElseThrow(() -> new RegraNegocioException("Mesa " + form.getMesaNumero() + " não existe."));

        // UC-1, fluxo alternativo do passo 4
        if (mesaOcupada(mesa)) {
            throw new RegraNegocioException("A mesa " + mesa.getNumero() + " já possui um pedido não finalizado.");
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioCpf)
                .orElseThrow(() -> new RegraNegocioException("Funcionário não encontrado."));

        Pedido pedido = new Pedido();
        pedido.setMesa(mesa);
        pedido.setFuncionario(funcionario);
        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus(StatusPedido.AGUARDANDO_PREPARO);

        for (Map.Entry<Long, Integer> entrada : form.getQuantidades().entrySet()) {
            Integer quantidade = entrada.getValue();
            if (quantidade == null || quantidade == 0) {
                continue;
            }
            if (quantidade < 0) {
                throw new RegraNegocioException("Quantidade não pode ser negativa.");
            }
            Prato prato = pratoRepository.findById(entrada.getKey())
                    .orElseThrow(() -> new RegraNegocioException("Prato não encontrado."));
            if (!prato.isDisponivel()) {
                throw new RegraNegocioException("O prato " + prato.getNome() + " não está disponível.");
            }
            BigDecimal preco = precoService.precoAtual(prato);
            pedido.adicionarItem(new ItemPedido(prato, quantidade, preco));
        }

        if (pedido.getItens().isEmpty()) {
            throw new RegraNegocioException("Adicione pelo menos um prato ao pedido.");
        }
        return pedidoRepository.save(pedido);
    }

    /** Avança para o próximo status do fluxo (RF012). */
    public Pedido avancarStatus(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        StatusPedido proximo = pedido.getStatus().proximo();
        if (proximo == null) {
            throw new RegraNegocioException("O pedido #" + pedidoId + " já está " + pedido.getStatus().getDescricao().toLowerCase() + ".");
        }
        pedido.setStatus(proximo);
        return pedido;
    }

    public Pedido cancelar(Long pedidoId) {
        Pedido pedido = buscar(pedidoId);
        if (!pedido.getStatus().isAberto()) {
            throw new RegraNegocioException("Só é possível cancelar pedidos em andamento.");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return pedido;
    }

    @Transactional(readOnly = true)
    public Pedido buscar(Long pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido #" + pedidoId + " não encontrado."));
    }
}
