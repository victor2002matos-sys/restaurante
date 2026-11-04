package br.com.restaurante.controller;

import br.com.restaurante.domain.Pedido;
import br.com.restaurante.domain.StatusPedido;
import br.com.restaurante.repository.PedidoRepository;
import br.com.restaurante.service.PedidoService;
import br.com.restaurante.service.RegraNegocioException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;

/** Painel da cozinha: pedidos a preparar e atualização do preparo (ator Cozinheiro). */
@Controller
@RequestMapping("/cozinha")
public class CozinhaController {

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public CozinhaController(PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String painel(Model model) {
        model.addAttribute("aguardando", pedidoRepository.findByStatusInOrderByDataHoraAsc(EnumSet.of(StatusPedido.AGUARDANDO_PREPARO)));
        model.addAttribute("emPreparo", pedidoRepository.findByStatusInOrderByDataHoraAsc(EnumSet.of(StatusPedido.EM_PREPARO)));
        model.addAttribute("prontos", pedidoRepository.findByStatusInOrderByDataHoraAsc(EnumSet.of(StatusPedido.PRONTO)));
        return "cozinha/painel";
    }

    /** A cozinha só move o pedido até "Pronto"; a entrega é com o garçom. */
    @PostMapping("/{id}/avancar")
    public String avancar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.buscar(id);
            if (pedido.getStatus() != StatusPedido.AGUARDANDO_PREPARO && pedido.getStatus() != StatusPedido.EM_PREPARO) {
                throw new RegraNegocioException("O pedido #" + id + " não está mais na cozinha.");
            }
            pedido = pedidoService.avancarStatus(id);
            redirect.addFlashAttribute("sucesso", "Pedido #" + id + ": " + pedido.getStatus().getDescricao() + ".");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/cozinha";
    }
}
