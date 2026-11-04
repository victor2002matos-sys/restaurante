package br.com.restaurante.controller.admin;

import br.com.restaurante.domain.Mesa;
import br.com.restaurante.repository.MesaRepository;
import br.com.restaurante.repository.PedidoRepository;
import br.com.restaurante.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.stream.Collectors;

/** Controle das mesas do restaurante (RF009). */
@Controller
@RequestMapping("/admin/mesas")
public class MesaAdminController {

    private final MesaRepository mesaRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public MesaAdminController(MesaRepository mesaRepository, PedidoRepository pedidoRepository, PedidoService pedidoService) {
        this.mesaRepository = mesaRepository;
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(Model model) {
        var mesas = mesaRepository.findAllByOrderByNumeroAsc();
        Set<Integer> ocupadas = mesas.stream().filter(pedidoService::mesaOcupada)
                .map(Mesa::getNumero).collect(Collectors.toSet());
        model.addAttribute("mesas", mesas);
        model.addAttribute("ocupadas", ocupadas);
        return "admin/mesas";
    }

    @PostMapping
    public String adicionar(@RequestParam(required = false) Integer numero,
                            @RequestParam(required = false) Integer capacidade, RedirectAttributes redirect) {
        if (numero == null || numero < 1) {
            redirect.addFlashAttribute("erro", "Informe um número de mesa válido.");
        } else if (capacidade != null && capacidade < 1) {
            redirect.addFlashAttribute("erro", "A capacidade deve ser positiva.");
        } else if (mesaRepository.existsById(numero)) {
            redirect.addFlashAttribute("erro", "A mesa " + numero + " já existe.");
        } else {
            mesaRepository.save(new Mesa(numero, capacidade));
            redirect.addFlashAttribute("sucesso", "Mesa " + numero + " adicionada.");
        }
        return "redirect:/admin/mesas";
    }

    @PostMapping("/{numero}/excluir")
    public String excluir(@PathVariable Integer numero, RedirectAttributes redirect) {
        Mesa mesa = mesaRepository.findById(numero).orElseThrow();
        if (pedidoRepository.existsByMesa(mesa)) {
            redirect.addFlashAttribute("erro", "A mesa " + numero + " tem pedidos registrados e não pode ser excluída.");
        } else {
            mesaRepository.delete(mesa);
            redirect.addFlashAttribute("sucesso", "Mesa " + numero + " excluída.");
        }
        return "redirect:/admin/mesas";
    }
}
