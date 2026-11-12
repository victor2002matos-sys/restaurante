package br.com.restaurante.controller;

import br.com.restaurante.domain.Categorias;
import br.com.restaurante.domain.Prato;
import br.com.restaurante.repository.PratoRepository;
import br.com.restaurante.service.PrecoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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
        model.addAttribute("categorias", Categorias.agrupar(pratos));
        model.addAttribute("precos", precoService.precosAtuais(pratos));
        return "cardapio/index";
    }

}
