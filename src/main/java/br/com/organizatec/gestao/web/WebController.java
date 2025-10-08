package br.com.organizatec.gestao.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.service.FuncionarioService;
import br.com.organizatec.gestao.service.TerceirizadoService;
import br.com.organizatec.gestao.service.VisitanteService;

/**
 * Controller Web principal (Thymeleaf).
 * Mostra o dashboard unificado e navegações para formulários de cadastro.
 */
@Controller
public class WebController {

    private final FuncionarioService funcionarioService;
    private final TerceirizadoService terceirizadoService;
    private final VisitanteService visitanteService;

    public WebController(FuncionarioService funcionarioService,
                         TerceirizadoService terceirizadoService,
                         VisitanteService visitanteService) {
        this.funcionarioService = funcionarioService;
        this.terceirizadoService = terceirizadoService;
        this.visitanteService = visitanteService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        List<RowVM> linhas = new ArrayList<>();

        // Segurança: garante que nenhuma lista seja nula
        List<FuncionarioProprio> funcionarios = funcionarioService.buscarTodos();
        List<Terceirizado> terceirizados = terceirizadoService.listarTodos();
        List<Visitante> visitantes = visitanteService.listarTodos();

        if (funcionarios != null) {
            for (FuncionarioProprio f : funcionarios) {
                linhas.add(new RowVM(f.getNome(), "Funcionário", "Interno"));
            }
        }

        if (terceirizados != null) {
            for (Terceirizado t : terceirizados) {
                linhas.add(new RowVM(t.getNome(), "Terceirizado",
                        statusAcesso(t.getDataHoraEntrada(), t.getDataHoraSaida())));
            }
        }

        if (visitantes != null) {
            for (Visitante v : visitantes) {
                linhas.add(new RowVM(v.getNome(), "Visitante",
                        statusAcesso(v.getDataHoraEntrada(), v.getDataHoraSaida())));
            }
        }

        linhas.sort(Comparator.comparing(RowVM::tipo).thenComparing(RowVM::nome));
        model.addAttribute("linhas", linhas);
        return "home";
    }

    private String statusAcesso(LocalDateTime entrada, LocalDateTime saida) {
        if (entrada != null && (saida == null || saida.isBefore(entrada))) {
            return "Dentro";
        }
        return "Fora";
    }

    public record RowVM(String nome, String tipo, String status) {}
}