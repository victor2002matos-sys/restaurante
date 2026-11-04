package br.com.restaurante.controller.admin;

import br.com.restaurante.repository.AvaliacaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/** Relatório das avaliações recebidas (RF013). */
@Controller
@RequestMapping("/admin/avaliacoes")
public class AvaliacaoAdminController {

    private final AvaliacaoRepository avaliacaoRepository;

    public AvaliacaoAdminController(AvaliacaoRepository avaliacaoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("avaliacoes", avaliacaoRepository.findAllByOrderByDataHoraDesc());
        model.addAttribute("media", avaliacaoRepository.mediaNotas());
        return "admin/avaliacoes";
    }
}
