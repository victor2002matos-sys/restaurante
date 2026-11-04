package br.com.restaurante.service;

import br.com.restaurante.domain.Prato;
import br.com.restaurante.repository.ItemPedidoRepository;
import br.com.restaurante.repository.PratoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Cadastro de pratos (UC-2). */
@Service
@Transactional
public class PratoService {

    private final PratoRepository pratoRepository;
    private final ItemPedidoRepository itemPedidoRepository;

    public PratoService(PratoRepository pratoRepository, ItemPedidoRepository itemPedidoRepository) {
        this.pratoRepository = pratoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
    }

    public Prato salvar(Prato prato) {
        String nome = prato.getNome().trim();
        boolean duplicado = prato.getId() == null
                ? pratoRepository.existsByNomeIgnoreCase(nome)
                : pratoRepository.existsByNomeIgnoreCaseAndIdNot(nome, prato.getId());
        if (duplicado) {
            // UC-2, fluxo alternativo do passo 7
            throw new RegraNegocioException("Já existe um prato chamado \"" + nome + "\". Use um nome diferente.");
        }
        prato.setNome(nome);
        return pratoRepository.save(prato);
    }

    /**
     * Prato que já foi pedido não é apagado, só fica indisponível, para manter o histórico.
     * @return true se o prato foi excluído, false se foi apenas marcado como indisponível
     */
    public boolean excluir(Long id) {
        Prato prato = pratoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Prato não encontrado."));
        if (itemPedidoRepository.existsByPrato(prato)) {
            prato.setDisponivel(false);
            return false;
        }
        pratoRepository.delete(prato);
        return true;
    }
}
