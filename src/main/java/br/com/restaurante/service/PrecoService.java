package br.com.restaurante.service;

import br.com.restaurante.domain.Prato;
import br.com.restaurante.domain.Promocao;
import br.com.restaurante.repository.PromocaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/** Calcula o preço de cada prato considerando as promoções vigentes (RF007, RF008). */
@Service
@Transactional(readOnly = true)
public class PrecoService {

    private final PromocaoRepository promocaoRepository;

    public PrecoService(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = promocaoRepository;
    }

    /** Menor preço entre as promoções vigentes hoje; o preço normal se não houver promoção. */
    public BigDecimal precoAtual(Prato prato) {
        return precosAtuais(List.of(prato)).get(prato.getId());
    }

    /** Preço atual de cada prato, indexado pelo id. */
    public Map<Long, BigDecimal> precosAtuais(Collection<Prato> pratos) {
        List<Promocao> vigentes = promocaoRepository.findVigentes(LocalDate.now());
        Map<Long, BigDecimal> precos = new HashMap<>();
        for (Prato prato : pratos) {
            BigDecimal melhor = prato.getPreco();
            for (Promocao promocao : vigentes) {
                boolean incluiPrato = promocao.getPratos().stream().anyMatch(p -> p.getId().equals(prato.getId()));
                if (incluiPrato) {
                    melhor = melhor.min(promocao.aplicar(prato.getPreco()));
                }
            }
            precos.put(prato.getId(), melhor);
        }
        return precos;
    }
}
