package br.com.restaurante.controller;

import br.com.restaurante.domain.StatusPedido;
import br.com.restaurante.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.EnumSet;

@Controller
public class HomeController {

    private final PedidoRepository pedidoRepository;
    private final MesaRepository mesaRepository;
    private final PratoRepository pratoRepository;
    private final ClienteRepository clienteRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public HomeController(PedidoRepository pedidoRepository, MesaRepository mesaRepository,
                          PratoRepository pratoRepository, ClienteRepository clienteRepository,
                          AvaliacaoRepository avaliacaoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.mesaRepository = mesaRepository;
        this.pratoRepository = pratoRepository;
        this.clienteRepository = clienteRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @GetMapping("/")
    public String inicio() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /** Painel inicial dos funcionários com os números do dia. */
    @GetMapping("/painel")
    public String painel(Model model) {
        var abertos = EnumSet.of(StatusPedido.AGUARDANDO_PREPARO, StatusPedido.EM_PREPARO,
                StatusPedido.PRONTO, StatusPedido.ENTREGUE);
        var pedidosAbertos = pedidoRepository.findByStatusInOrderByDataHoraAsc(abertos);
        model.addAttribute("pedidosAbertos", pedidosAbertos);
        model.addAttribute("totalMesas", mesaRepository.count());
        model.addAttribute("mesasOcupadas", pedidosAbertos.stream().map(p -> p.getMesa().getNumero()).distinct().count());
        model.addAttribute("totalPratos", pratoRepository.count());
        model.addAttribute("totalClientes", clienteRepository.count());
        model.addAttribute("mediaAvaliacoes", avaliacaoRepository.mediaNotas());
        return "painel";
    }
}
