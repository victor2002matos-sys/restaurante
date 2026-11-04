package br.com.restaurante.controller.admin;

import br.com.restaurante.domain.Funcao;
import br.com.restaurante.domain.Funcionario;
import br.com.restaurante.domain.Turno;
import br.com.restaurante.repository.FuncionarioRepository;
import br.com.restaurante.repository.PessoaRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Cadastro de funcionários e suas senhas de acesso (RF001, RF003). */
@Controller
@RequestMapping("/admin/funcionarios")
public class FuncionarioAdminController {

    private final FuncionarioRepository funcionarioRepository;
    private final PessoaRepository pessoaRepository;
    private final PasswordEncoder passwordEncoder;

    public FuncionarioAdminController(FuncionarioRepository funcionarioRepository, PessoaRepository pessoaRepository,
                                      PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.pessoaRepository = pessoaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("funcionarios", funcionarioRepository.findAllByOrderByNomeAsc());
        return "admin/funcionarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Funcionario(), true, model);
    }

    @GetMapping("/{cpf}/editar")
    public String editar(@PathVariable String cpf, Model model) {
        return formulario(funcionarioRepository.findById(cpf).orElseThrow(), false, model);
    }

    @PostMapping
    public String salvar(@RequestParam(defaultValue = "false") boolean novo,
                         @RequestParam(defaultValue = "") String novaSenha,
                         @Valid @ModelAttribute("funcionario") Funcionario funcionario, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (funcionario.getCpf() != null) {
            funcionario.setCpf(funcionario.getCpf().replaceAll("\\D", ""));
        }
        if (novo && !result.hasFieldErrors("cpf") && pessoaRepository.existsById(funcionario.getCpf())) {
            result.rejectValue("cpf", "duplicado", "Já existe uma pessoa cadastrada com este CPF.");
        }
        if (novo && novaSenha.length() < 6) {
            model.addAttribute("erroSenha", "A senha deve ter pelo menos 6 caracteres.");
        } else if (!novo && !novaSenha.isEmpty() && novaSenha.length() < 6) {
            model.addAttribute("erroSenha", "A nova senha deve ter pelo menos 6 caracteres.");
        }
        if (result.hasErrors() || model.containsAttribute("erroSenha")) {
            return formulario(funcionario, novo, model);
        }

        if (novaSenha.isEmpty()) {
            // Edição sem trocar a senha: mantém a atual
            funcionario.setSenha(funcionarioRepository.findById(funcionario.getCpf()).orElseThrow().getSenha());
        } else {
            funcionario.setSenha(passwordEncoder.encode(novaSenha));
        }
        funcionarioRepository.save(funcionario);
        redirect.addFlashAttribute("sucesso", "Funcionário " + funcionario.getNome() + " salvo.");
        return "redirect:/admin/funcionarios";
    }

    @PostMapping("/{cpf}/excluir")
    public String excluir(@PathVariable String cpf, Authentication auth, RedirectAttributes redirect) {
        if (cpf.equals(auth.getName())) {
            redirect.addFlashAttribute("erro", "Você não pode excluir o próprio usuário.");
            return "redirect:/admin/funcionarios";
        }
        try {
            funcionarioRepository.deleteById(cpf);
            funcionarioRepository.flush();
            redirect.addFlashAttribute("sucesso", "Funcionário excluído.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: o funcionário tem pedidos registrados.");
        }
        return "redirect:/admin/funcionarios";
    }

    private String formulario(Funcionario funcionario, boolean novo, Model model) {
        model.addAttribute("funcionario", funcionario);
        model.addAttribute("novo", novo);
        model.addAttribute("funcoes", Funcao.values());
        model.addAttribute("turnos", Turno.values());
        return "admin/funcionarios/form";
    }
}
