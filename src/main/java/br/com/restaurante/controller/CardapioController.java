package br.com.restaurante.controller;

import br.com.restaurante.domain.Prato;
import br.com.restaurante.repository.PratoRepository;
import br.com.restaurante.service.PrecoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Cardápio público com os pratos disponíveis e preços promocionais (RF005). */
@Controller
public class CardapioController {

    private final PratoRepository pratoRepository;
    private final PrecoService precoService;

    public CardapioController(PratoRepository pratoRepository, PrecoService precoService) {
        this.pratoRepository = pratoRepository;
        this.precoService = precoService;
    }

    @GetMapping("/cardapio")
    public String cardapio(Model model) {
        List<Prato> pratos = pratoRepository.findByDisponivelTrueOrderByCategoriaAscNomeAsc();
        Map<String, List<Prato>> porCategoria = pratos.stream()
                .sorted(Comparator.comparingInt((Prato p) -> ordemCategoria(p.getCategoria())))
                .collect(Collectors.groupingBy(Prato::getCategoria, LinkedHashMap::new, Collectors.toList()));
        model.addAttribute("categorias", porCategoria);
        model.addAttribute("precos", precoService.precosAtuais(pratos));
        return "cardapio/index";
    }

    /** Ordem das seções no cardápio; categorias novas vão para o final. */
    private static final List<String> ORDEM = List.of("Antipasti", "Massas", "Risotti", "Dolci", "Bevande");

    private int ordemCategoria(String categoria) {
        int i = ORDEM.indexOf(categoria);
        return i < 0 ? ORDEM.size() : i;
    }
}
