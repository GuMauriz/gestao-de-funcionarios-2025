package br.com.organizatec.gestao.web;

import java.time.LocalDate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.service.FuncionarioService;
import br.com.organizatec.gestao.service.TerceirizadoService;
import br.com.organizatec.gestao.service.VisitanteService;

/**
 * Controlador para formulários de cadastro via Thymeleaf.
 */
@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private final FuncionarioService funcionarioService;
    private final TerceirizadoService terceirizadoService;
    private final VisitanteService visitanteService;

    public CadastroController(FuncionarioService funcionarioService,
                              TerceirizadoService terceirizadoService,
                              VisitanteService visitanteService) {
        this.funcionarioService = funcionarioService;
        this.terceirizadoService = terceirizadoService;
        this.visitanteService = visitanteService;
    }

    // === FUNCIONÁRIO (RH) ===
    @GetMapping("/funcionario")
    public String formFuncionario(Model model) {
        model.addAttribute("funcionario", new FuncionarioProprio());
        return "cadastro-funcionario";
    }

    @PostMapping("/funcionario")
    public String salvarFuncionario(@ModelAttribute FuncionarioProprio f) {
        f.setDataContratacao(LocalDate.now());
        funcionarioService.criarFuncionario(f);
        return "redirect:/home";
    }

    // === TERCEIRIZADO (RH) ===
    @GetMapping("/terceirizado")
    public String formTerceirizado(Model model) {
        model.addAttribute("terceirizado", new Terceirizado());
        return "cadastro-terceirizado";
    }

    @PostMapping("/terceirizado")
    public String salvarTerceirizado(@ModelAttribute Terceirizado t) {
        terceirizadoService.criar(t);
        return "redirect:/home";
    }

    // === VISITANTE (Recepção) ===
    @GetMapping("/visitante")
    public String formVisitante(Model model) {
        model.addAttribute("visitante", new Visitante());
        return "cadastro-visitante";
    }

    @PostMapping("/visitante")
    public String salvarVisitante(@ModelAttribute Visitante v) {
        visitanteService.criar(v);
        return "redirect:/home";
    }
}