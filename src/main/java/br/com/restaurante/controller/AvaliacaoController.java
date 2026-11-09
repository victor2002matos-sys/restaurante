package br.com.restaurante.controller;

import br.com.restaurante.domain.Pedido;
import br.com.restaurante.service.AvaliacaoService;
import br.com.restaurante.service.RegraNegocioException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/** Avaliação do atendimento pelo cliente, sem login (UC-3). */
@Controller
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    /** Passo 2: pede o número do pedido e o CPF. */
    @GetMapping
    public String identificar(@RequestParam(required = false) Long pedido, Model model) {
        model.addAttribute("pedidoId", pedido);
        return "avaliacao/identificar";
    }

    /** Passo 4: valida o pedido e libera nota e comentário. */
    @PostMapping("/validar")
    public String validar(@RequestParam(required = false) Long pedidoId, @RequestParam(required = false) String cpf, Model model) {
        try {
            Pedido pedido = avaliacaoService.validarPedido(pedidoId, cpf);
            model.addAttribute("pedido", pedido);
            model.addAttribute("cpf", cpf);
            return "avaliacao/formulario";
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("pedidoId", pedidoId);
            model.addAttribute("cpf", cpf);
            return "avaliacao/identificar";
        }
    }

    /** Passos 6 a 8: confere a nota, salva e agradece. */
    @PostMapping
    public String enviar(@RequestParam Long pedidoId, @RequestParam(required = false) String cpf,
                         @RequestParam(required = false) Integer nota,
                         @RequestParam(required = false) String comentario, Model model) {
        try {
            avaliacaoService.avaliar(pedidoId, cpf, nota, comentario);
            return "avaliacao/obrigado";
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("cpf", cpf);
            model.addAttribute("nota", nota);
            model.addAttribute("comentario", comentario);
            try {
                model.addAttribute("pedido", avaliacaoService.validarPedido(pedidoId, cpf));
                return "avaliacao/formulario";
            } catch (RegraNegocioException outro) {
                model.addAttribute("erro", outro.getMessage());
                model.addAttribute("pedidoId", pedidoId);
                return "avaliacao/identificar";
            }
        }
    }
}
