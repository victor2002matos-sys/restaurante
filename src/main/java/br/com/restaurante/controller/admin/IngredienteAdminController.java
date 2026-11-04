package br.com.restaurante.controller.admin;

import br.com.restaurante.domain.Ingrediente;
import br.com.restaurante.repository.IngredienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Cadastro de ingredientes, pré-requisito do UC-2. */
@Controller
@RequestMapping("/admin/ingredientes")
public class IngredienteAdminController {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteAdminController(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ingredientes", ingredienteRepository.findAllByOrderByNomeAsc());
        return "admin/ingredientes";
    }

    @PostMapping
    public String adicionar(@RequestParam String nome, RedirectAttributes redirect) {
        String limpo = nome.trim();
        if (limpo.isEmpty()) {
            redirect.addFlashAttribute("erro", "Informe o nome do ingrediente.");
        } else if (ingredienteRepository.existsByNomeIgnoreCase(limpo)) {
            redirect.addFlashAttribute("erro", "O ingrediente \"" + limpo + "\" já existe.");
        } else {
            ingredienteRepository.save(new Ingrediente(limpo));
            redirect.addFlashAttribute("sucesso", "Ingrediente \"" + limpo + "\" adicionado.");
        }
        return "redirect:/admin/ingredientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            ingredienteRepository.deleteById(id);
            ingredienteRepository.flush();
            redirect.addFlashAttribute("sucesso", "Ingrediente excluído.");
        } catch (DataIntegrityViolationException e) {
            redirect.addFlashAttribute("erro", "Não é possível excluir: o ingrediente está associado a algum prato.");
        }
        return "redirect:/admin/ingredientes";
    }
}
