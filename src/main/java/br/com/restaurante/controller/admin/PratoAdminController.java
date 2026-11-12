package br.com.restaurante.controller.admin;

import br.com.restaurante.domain.Categorias;
import br.com.restaurante.domain.Prato;
import br.com.restaurante.repository.IngredienteRepository;
import br.com.restaurante.repository.PratoRepository;
import br.com.restaurante.service.PratoService;
import br.com.restaurante.service.PrecoService;
import br.com.restaurante.service.RegraNegocioException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Gerenciamento do cardápio pelo gerente (UC-2, RF004, RF006). */
@Controller
@RequestMapping("/admin/pratos")
public class PratoAdminController {

    private final PratoRepository pratoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final PratoService pratoService;
    private final PrecoService precoService;

    public PratoAdminController(PratoRepository pratoRepository, IngredienteRepository ingredienteRepository,
                                PratoService pratoService, PrecoService precoService) {
        this.pratoRepository = pratoRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.pratoService = pratoService;
        this.precoService = precoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Prato> pratos = pratoRepository.findAllByOrderByCategoriaAscNomeAsc();
        model.addAttribute("pratos", pratos);
        model.addAttribute("precos", precoService.precosAtuais(pratos));
        return "admin/pratos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        return formulario(new Prato(), model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(pratoRepository.findById(id).orElseThrow(), model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("prato") Prato prato, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        // UC-2, fluxo alternativo do passo 6: dados inválidos voltam para o formulário com as mensagens
        if (result.hasErrors()) {
            return formulario(prato, model);
        }
        try {
            pratoService.salvar(prato);
        } catch (RegraNegocioException e) {
            result.rejectValue("nome", "duplicado", e.getMessage());
            return formulario(prato, model);
        }
        redirect.addFlashAttribute("sucesso", "Prato cadastrado com sucesso.");
        return "redirect:/admin/pratos";
    }

    @PostMapping("/{id}/disponibilidade")
    public String alternarDisponibilidade(@PathVariable Long id, RedirectAttributes redirect) {
        Prato prato = pratoRepository.findById(id).orElseThrow();
        prato.setDisponivel(!prato.isDisponivel());
        pratoRepository.save(prato);
        redirect.addFlashAttribute("sucesso", prato.getNome() + (prato.isDisponivel() ? " voltou ao cardápio." : " saiu do cardápio."));
        return "redirect:/admin/pratos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        if (pratoService.excluir(id)) {
            redirect.addFlashAttribute("sucesso", "Prato excluído.");
        } else {
            redirect.addFlashAttribute("erro", "O prato já aparece em pedidos, então foi marcado como indisponível em vez de excluído.");
        }
        return "redirect:/admin/pratos";
    }

    private String formulario(Prato prato, Model model) {
        model.addAttribute("prato", prato);
        model.addAttribute("ingredientes", ingredienteRepository.findAllByOrderByNomeAsc());
        // As quatro categorias padrão primeiro, depois outras que o gerente já tenha criado
        Set<String> categorias = new LinkedHashSet<>(Categorias.ORDEM);
        categorias.addAll(pratoRepository.findCategorias());
        model.addAttribute("categorias", categorias);
        return "admin/pratos/form";
    }
}
