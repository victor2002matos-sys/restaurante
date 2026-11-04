package br.com.restaurante.controller;

import br.com.restaurante.domain.Cliente;
import br.com.restaurante.repository.ClienteRepository;
import br.com.restaurante.repository.PedidoRepository;
import br.com.restaurante.repository.PessoaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Cadastro de clientes (RF001, RF002). Usado por garçons e gerente. */
@Controller
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteRepository clienteRepository;
    private final PessoaRepository pessoaRepository;
    private final PedidoRepository pedidoRepository;

    public ClienteController(ClienteRepository clienteRepository, PessoaRepository pessoaRepository,
                             PedidoRepository pedidoRepository) {
        this.clienteRepository = clienteRepository;
        this.pessoaRepository = pessoaRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", clienteRepository.findAllByOrderByNomeAsc());
        return "clientes/lista";
    }

    @GetMapping("/novo")
    public String novo(@RequestParam(required = false) String cpf, Model model) {
        Cliente cliente = new Cliente();
        cliente.setCpf(cpf);
        model.addAttribute("cliente", cliente);
        model.addAttribute("novo", true);
        return "clientes/form";
    }

    @GetMapping("/{cpf}/editar")
    public String editar(@PathVariable String cpf, Model model) {
        model.addAttribute("cliente", clienteRepository.findById(cpf).orElseThrow());
        model.addAttribute("novo", false);
        return "clientes/form";
    }

    @PostMapping
    public String salvar(@RequestParam(defaultValue = "false") boolean novo,
                         @Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (cliente.getCpf() != null) {
            cliente.setCpf(cliente.getCpf().replaceAll("\\D", ""));
        }
        if (novo && !result.hasFieldErrors("cpf") && pessoaRepository.existsById(cliente.getCpf())) {
            result.rejectValue("cpf", "duplicado", "Já existe uma pessoa cadastrada com este CPF.");
        }
        if (result.hasErrors()) {
            model.addAttribute("novo", novo);
            return "clientes/form";
        }
        clienteRepository.save(cliente);
        redirect.addFlashAttribute("sucesso", "Cliente " + cliente.getNome() + " salvo com sucesso.");
        return "redirect:/clientes";
    }

    @PostMapping("/{cpf}/excluir")
    public String excluir(@PathVariable String cpf, RedirectAttributes redirect) {
        Cliente cliente = clienteRepository.findById(cpf).orElseThrow();
        if (pedidoRepository.existsByCliente(cliente)) {
            redirect.addFlashAttribute("erro", "Não é possível excluir " + cliente.getNome() + ": há pedidos registrados para este cliente.");
        } else {
            clienteRepository.delete(cliente);
            redirect.addFlashAttribute("sucesso", "Cliente excluído.");
        }
        return "redirect:/clientes";
    }
}
