package br.com.restaurante.controller.admin;

import br.com.restaurante.domain.Promocao;
import br.com.restaurante.domain.TipoDesconto;
import br.com.restaurante.repository.PratoRepository;
import br.com.restaurante.repository.PromocaoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/** Cadastro de promoções e associação com pratos (RF007, RF008). */
@Controller
@RequestMapping("/admin/promocoes")
public class PromocaoAdminController {

    private final PromocaoRepository promocaoRepository;
    private final PratoRepository pratoRepository;

    public PromocaoAdminController(PromocaoRepository promocaoRepository, PratoRepository pratoRepository) {
        this.promocaoRepository = promocaoRepository;
        this.pratoRepository = pratoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("promocoes", promocaoRepository.findAllByOrderByDataInicioDesc());
        model.addAttribute("hoje", LocalDate.now());
        return "admin/promocoes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        Promocao promocao = new Promocao();
        promocao.setDataInicio(LocalDate.now());
        promocao.setDataFim(LocalDate.now().plusDays(7));
        promocao.setTipoDesconto(TipoDesconto.PERCENTUAL);
        return formulario(promocao, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        return formulario(promocaoRepository.findById(id).orElseThrow(), model);
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("promocao") Promocao promocao, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            return formulario(promocao, model);
        }
        promocaoRepository.save(promocao);
        redirect.addFlashAttribute("sucesso", "Promoção \"" + promocao.getNome() + "\" salva.");
        return "redirect:/admin/promocoes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        promocaoRepository.deleteById(id);
        redirect.addFlashAttribute("sucesso", "Promoção excluída. Pedidos antigos mantêm o preço cobrado.");
        return "redirect:/admin/promocoes";
    }

    private String formulario(Promocao promocao, Model model) {
        model.addAttribute("promocao", promocao);
        model.addAttribute("pratos", pratoRepository.findAllByOrderByCategoriaAscNomeAsc());
        model.addAttribute("tipos", TipoDesconto.values());
        return "admin/promocoes/form";
    }
}
