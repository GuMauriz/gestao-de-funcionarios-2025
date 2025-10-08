package br.com.organizatec.gestao.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;

@Service
public class RelatorioService {

    private final TerceirizadoRepository terceirizadoRepository;
    private final VisitanteRepository visitanteRepository;

    public RelatorioService(TerceirizadoRepository terceirizadoRepository,
                            VisitanteRepository visitanteRepository) {
        this.terceirizadoRepository = terceirizadoRepository;
        this.visitanteRepository = visitanteRepository;
    }

    /**
     * Imprime no console um resumo da circulação diária.
     * Formato:
     * [Tipo] Nome  |  Status: Dentro/Fora
     */
    @Transactional(readOnly = true)
    public void gerarRelatorioCirculacaoDiaria() {
        List<Terceirizado> terceirizados = terceirizadoRepository.findAll();
        List<Visitante> visitantes = visitanteRepository.findAll();

        System.out.println("=== RELATÓRIO: CIRCULAÇÃO DIÁRIA ===");
        terceirizados.forEach(t -> {
            String status = (t.getDataHoraEntrada() != null && t.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            System.out.printf("[Terceirizado] %s | Status: %s%n", t.getNome(), status);
        });
        visitantes.forEach(v -> {
            String status = (v.getDataHoraEntrada() != null && v.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            System.out.printf("[Visitante] %s | Status: %s%n", v.getNomeCompleto(), status);
        });
        System.out.println("=== FIM DO RELATÓRIO ===");
    }

    // --- EXPORTAÇÃO CSV ---
    /**
     * Gera o conteúdo CSV da circulação diária.
     * Colunas: Tipo, Nome, Documento, Status, Hora_Entrada
     */
    @Transactional(readOnly = true)
    public String exportarCirculacaoDiariaCSV() {
        var terceirizados = terceirizadoRepository.findAll();
        var visitantes = visitanteRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Tipo,Nome,Documento,Status,Hora_Entrada\n");

        // Terceirizados
        terceirizados.forEach(t -> {
            String status = (t.getDataHoraEntrada() != null && t.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            String hora = t.getDataHoraEntrada() != null ? t.getDataHoraEntrada().toString() : "";
            // Documento = CPF (Pessoa)
            sb.append("Terceirizado").append(',')
              .append(escapeCsv(t.getNome())).append(',')
              .append(escapeCsv(t.getCpf())).append(',')
              .append(status).append(',')
              .append(escapeCsv(hora)).append('\n');
        });

        // Visitantes
        visitantes.forEach(v -> {
            String status = (v.getDataHoraEntrada() != null && v.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            String hora = v.getDataHoraEntrada() != null ? v.getDataHoraEntrada().toString() : "";
            sb.append("Visitante").append(',')
              .append(escapeCsv(v.getNomeCompleto())).append(',')
              .append(escapeCsv(v.getDocumentoIdentificacao())).append(',')
              .append(status).append(',')
              .append(escapeCsv(hora)).append('\n');
        });

        return sb.toString();
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        boolean precisaAspas = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String val = s.replace("\"", "\"\"");
        return precisaAspas ? "\"" + val + "\"" : val;
    }

    // --- EXPORTAÇÃO PDF (ESQUELETO/STUB) ---
    /**
     * Simula a geração de um PDF (futuramente com OpenPDF/iText).
     * Aqui apenas devolvemos um byte[] com o conteúdo textual.
     */
    @Transactional(readOnly = true)
    public byte[] exportarRelatorioPDF(String titulo, java.util.List<String> dados) {
        StringBuilder sb = new StringBuilder();
        sb.append("PDF SIMULADO\n");
        sb.append("Título: ").append(titulo).append("\n\n");
        dados.forEach(l -> sb.append(l).append('\n'));
        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }
}