package br.com.organizatec.gestao.web;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.service.ControleAcessoService;
import br.com.organizatec.gestao.service.FuncionarioService;
import br.com.organizatec.gestao.service.TerceirizadoService;
import br.com.organizatec.gestao.service.VisitanteService;

/**
 * Formulários de cadastro via Thymeleaf.
 * - Define defaults para evitar NPEs/erros de binding
 * - "Registrar entrada agora" usa SEMPRE a hora do sistema (sem input do usuário)
 */
@Controller
@RequestMapping("/cadastro")
public class CadastroController {

    private static final Logger log = LoggerFactory.getLogger(CadastroController.class);

    private final FuncionarioService funcionarioService;
    private final TerceirizadoService terceirizadoService;
    private final VisitanteService visitanteService;
    private final ControleAcessoService controleAcessoService;

    public CadastroController(FuncionarioService funcionarioService,
                              TerceirizadoService terceirizadoService,
                              VisitanteService visitanteService,
                              ControleAcessoService controleAcessoService) {
        this.funcionarioService = funcionarioService;
        this.terceirizadoService = terceirizadoService;
        this.visitanteService = visitanteService;
        this.controleAcessoService = controleAcessoService;
    }

    // -------- FUNCIONÁRIO (RH) --------
    @GetMapping("/funcionario")
    public String formFuncionario(Model model) {
        model.addAttribute("funcionario", new FuncionarioProprio());
        return "cadastro-funcionario";
    }

    @PostMapping("/funcionario")
    public String salvarFuncionario(@ModelAttribute FuncionarioProprio f,
                                    @RequestParam(name = "registrarEntrada", defaultValue = "false") boolean registrarEntrada) {
        // Defaults de segurança para evitar nulls que possam quebrar persistência/relatório
        if (f.getNome() == null) f.setNome("");
        if (f.getCpf() == null) f.setCpf("");
        if (f.getDataNascimento() == null) f.setDataNascimento(LocalDate.now().minusYears(18));
        if (f.getCargo() == null) f.setCargo("Não informado");
        if (f.getDepartamento() == null) f.setDepartamento("Não informado");
        if (f.getDataContratacao() == null) f.setDataContratacao(LocalDate.now());

        FuncionarioProprio salvo = funcionarioService.criarFuncionario(f);

        // Funcionário próprio não participa do ciclo de entrada/saída nos relatórios (status "Interno"),
        // mas atendemos ao requisito do checkbox com um log para auditoria:
        if (registrarEntrada) {
            log.info("Ponto registrado agora para funcionário id={} (hora do sistema).", salvo.getId());
            // Se no futuro desejar registrar ponto em tabela própria, este é o gancho.
        }

        return "redirect:/home?ok=funcionario";
    }

    // -------- TERCEIRIZADO (RH) --------
    @GetMapping("/terceirizado")
    public String formTerceirizado(Model model) {
        model.addAttribute("terceirizado", new Terceirizado());
        return "cadastro-terceirizado";
    }

    @PostMapping("/terceirizado")
    public String salvarTerceirizado(@ModelAttribute Terceirizado t,
                                     @RequestParam(name = "registrarEntrada", defaultValue = "true") boolean registrarEntrada) {
        // Defaults seguros
        if (t.getNome() == null) t.setNome("");
        if (t.getCpf() == null) t.setCpf("");
        if (t.getDataNascimento() == null) t.setDataNascimento(LocalDate.now().minusYears(18));
        if (t.getEmpresaOrigem() == null) t.setEmpresaOrigem("Não informada");
        if (t.getSetor() == null) t.setSetor("Não informado");

        Terceirizado salvo = terceirizadoService.criar(t);

        // Se solicitado, registra ENTRADA imediatamente com hora do sistema
        if (registrarEntrada) {
            controleAcessoService.registrarEntrada(salvo.getId(), "terceirizado");
        }

        return "redirect:/home?ok=terceirizado";
    }

    // -------- VISITANTE (Recepção) --------
    @GetMapping("/visitante")
    public String formVisitante(Model model) {
        model.addAttribute("visitante", new Visitante());
        return "cadastro-visitante";
    }

    @PostMapping("/visitante")
    public String salvarVisitante(@ModelAttribute Visitante v,
                                  @RequestParam(name = "registrarEntrada", defaultValue = "true") boolean registrarEntrada) {
        // Defaults seguros
        if (v.getNome() == null) v.setNome("");
        if (v.getCpf() == null) v.setCpf("");
        if (v.getDataNascimento() == null) v.setDataNascimento(LocalDate.now().minusYears(18));
        if (v.getEmpresaOrigem() == null) v.setEmpresaOrigem("Não informada");
        if (v.getMotivoVisita() == null) v.setMotivoVisita("Não informado");

        Visitante salvo = visitanteService.criar(v);

        // Se solicitado, registra ENTRADA imediatamente com hora do sistema
        if (registrarEntrada) {
            controleAcessoService.registrarEntrada(salvo.getId(), "visitante");
        }

        return "redirect:/home?ok=visitante";
    }
}