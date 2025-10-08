package br.com.organizatec.gestao.web;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.organizatec.gestao.service.RelatorioService;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    @GetMapping(value = "/circulacao/csv", produces = "text/csv")
    public ResponseEntity<String> csv() {
        String csv = relatorioService.exportarCirculacaoDiariaCSV();
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("text/csv"))
                .body(csv != null ? csv : "");
    }

    @GetMapping("/circulacao/pdf")
    public ResponseEntity<byte[]> pdf() {
        byte[] pdf = relatorioService.exportarRelatorioPDF("Relatório de Circulação Diária");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename("circulacao_diaria.pdf").build());
        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}