package br.com.restaurante.service;

import br.com.restaurante.domain.Avaliacao;
import br.com.restaurante.domain.Pedido;
import br.com.restaurante.domain.StatusPedido;
import br.com.restaurante.repository.AvaliacaoRepository;
import br.com.restaurante.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/** Avaliação do atendimento pelo cliente (RF013, UC-3). */
@Service
@Transactional
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoRepository pedidoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, PedidoRepository pedidoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    /**
     * Confere se o pedido pode ser avaliado (UC-3, passo 4). O CPF confirma que quem avalia
     * é o cliente do pedido, já que a avaliação é feita sem login.
     */
    @Transactional(readOnly = true)
    public Pedido validarPedido(Long pedidoId, String cpf) {
        if (pedidoId == null) {
            throw new RegraNegocioException("Informe o número do pedido.");
        }
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido #" + pedidoId + " não encontrado."));
        String cpfLimpo = cpf == null ? "" : cpf.replaceAll("\\D", "");
        // pedido sem cliente identificado: basta o número do pedido
        if (pedido.getCliente() != null && !pedido.getCliente().getCpf().equals(cpfLimpo)) {
            throw new RegraNegocioException("O CPF informado não corresponde ao cliente do pedido.");
        }
        if (pedido.getStatus() != StatusPedido.FINALIZADO) {
            throw new RegraNegocioException("Só é possível avaliar pedidos finalizados. Status atual: "
                    + pedido.getStatus().getDescricao() + ".");
        }
        if (avaliacaoRepository.existsByPedidoId(pedidoId)) {
            throw new RegraNegocioException("Este pedido já foi avaliado. Obrigado!");
        }
        return pedido;
    }

    public Avaliacao avaliar(Long pedidoId, String cpf, Integer nota, String comentario) {
        Pedido pedido = validarPedido(pedidoId, cpf);
        // RN004 / UC-3 fluxo alternativo do passo 6
        if (nota == null || nota < 1 || nota > 10) {
            throw new RegraNegocioException("A nota deve estar entre 1 e 10.");
        }
        if (comentario != null && comentario.length() > 500) {
            throw new RegraNegocioException("O comentário pode ter no máximo 500 caracteres.");
        }
        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setPedido(pedido);
        avaliacao.setNota(nota);
        avaliacao.setComentario(comentario == null || comentario.isBlank() ? null : comentario.trim());
        avaliacao.setDataHora(LocalDateTime.now());
        return avaliacaoRepository.save(avaliacao);
    }
}
