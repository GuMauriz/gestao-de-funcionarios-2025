package br.com.organizatec.gestao.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return """
        <h2>Gestão de Funcionários 2025</h2>
        <p>API ativa e pronta para uso.</p>
        <ul>
            <li><a href="/funcionarios">/funcionarios</a> — Listar Funcionários</li>
            <li><a href="/relatorios/circulacao/csv">/relatorios/circulacao/csv</a> — Relatório CSV (RH)</li>
            <li><a href="/relatorios/circulacao/pdf">/relatorios/circulacao/pdf</a> — Relatório PDF (RH)</li>
        </ul>
        """;
    }
}