package br.com.organizatec.gestao.web;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.service.RelatorioService;

@RestController
@RequestMapping("/relatorios/circulacao")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    // GET /relatorios/circulacao/csv
    @GetMapping(value = "/csv", produces = "text/csv")
    public ResponseEntity<byte[]> csv() {
        String csv = relatorioService.exportarCirculacaoDiariaCSV();
        byte[] bytes = csv.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename("circulacao_diaria.csv").build());
        headers.set(HttpHeaders.CONTENT_ENCODING, "UTF-8");

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    // GET /relatorios/circulacao/pdf
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf() {
        byte[] pdf = relatorioService.exportarRelatorioPDF("Relatório de Circulação Diária");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("circulacao_diaria.pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
