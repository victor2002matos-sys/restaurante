package br.com.restaurante.controller;

import br.com.restaurante.domain.Mesa;
import br.com.restaurante.domain.Pedido;
import br.com.restaurante.domain.Prato;
import br.com.restaurante.repository.ClienteRepository;
import br.com.restaurante.repository.MesaRepository;
import br.com.restaurante.repository.PedidoRepository;
import br.com.restaurante.repository.PratoRepository;
import br.com.restaurante.service.NovoPedidoForm;
import br.com.restaurante.service.PedidoService;
import br.com.restaurante.service.PrecoService;
import br.com.restaurante.service.RegraNegocioException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** Registro e acompanhamento de pedidos pelo garçom (UC-1, RF010–RF012). */
@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PratoRepository pratoRepository;
    private final ClienteRepository clienteRepository;
    private final PrecoService precoService;

    public PedidoController(PedidoService pedidoService, PedidoRepository pedidoRepository,
                            MesaRepository mesaRepository, PratoRepository pratoRepository,
                            ClienteRepository clienteRepository, PrecoService precoService) {
        this.pedidoService = pedidoService;
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pratoRepository = pratoRepository;
        this.clienteRepository = clienteRepository;
        this.precoService = precoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByDataHoraDesc());
        return "pedidos/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) Integer mesa, Model model) {
        NovoPedidoForm form = new NovoPedidoForm();
        form.setMesaNumero(mesa);
        prepararFormulario(form, model);
        return "pedidos/novo";
    }

    @PostMapping
    public String registrar(@ModelAttribute("form") NovoPedidoForm form, Authentication auth,
                            Model model, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.registrar(form, auth.getName());
            redirect.addFlashAttribute("sucesso", "Pedido #" + pedido.getId() + " registrado para a mesa "
                    + pedido.getMesa().getNumero() + ".");
            return "redirect:/pedidos/" + pedido.getId();
        } catch (RegraNegocioException e) {
            model.addAttribute("erro", e.getMessage());
            prepararFormulario(form, model);
            return "pedidos/novo";
        }
    }

    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {
        model.addAttribute("pedido", pedidoService.buscar(id));
        return "pedidos/detalhe";
    }

    @PostMapping("/{id}/avancar")
    public String avancar(@PathVariable Long id, @RequestParam(defaultValue = "") String voltar, RedirectAttributes redirect) {
        try {
            Pedido pedido = pedidoService.avancarStatus(id);
            redirect.addFlashAttribute("sucesso", "Pedido #" + id + " agora está " + pedido.getStatus().getDescricao().toLowerCase() + ".");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "lista".equals(voltar) ? "redirect:/pedidos" : "redirect:/pedidos/" + id;
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            pedidoService.cancelar(id);
            redirect.addFlashAttribute("sucesso", "Pedido #" + id + " cancelado.");
        } catch (RegraNegocioException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/pedidos/" + id;
    }

    private void prepararFormulario(NovoPedidoForm form, Model model) {
        List<Mesa> mesas = mesaRepository.findAllByOrderByNumeroAsc();
        Set<Integer> ocupadas = mesas.stream().filter(pedidoService::mesaOcupada)
                .map(Mesa::getNumero).collect(Collectors.toSet());
        List<Prato> pratos = pratoRepository.findByDisponivelTrueOrderByCategoriaAscNomeAsc();
        model.addAttribute("form", form);
        model.addAttribute("mesas", mesas);
        model.addAttribute("ocupadas", ocupadas);
        model.addAttribute("clientes", clienteRepository.findAllByOrderByNomeAsc());
        model.addAttribute("pratos", pratos);
        model.addAttribute("precos", precoService.precosAtuais(pratos));
    }
}
