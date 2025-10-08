package br.com.organizatec.gestao.service;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class RelatorioService {

    private final TerceirizadoRepository terceirizadoRepository;
    private final VisitanteRepository visitanteRepository;
    private final FuncionarioProprioRepository funcionarioRepository;

    public RelatorioService(TerceirizadoRepository terceirizadoRepository,
                            VisitanteRepository visitanteRepository,
                            FuncionarioProprioRepository funcionarioRepository) {
        this.terceirizadoRepository = terceirizadoRepository;
        this.visitanteRepository = visitanteRepository;
        this.funcionarioRepository = funcionarioRepository;
    }

    /**
     * Imprime no console um resumo da circulação diária.
     * Agora inclui Funcionários Próprios com status "Interno".
     */
    @Transactional(readOnly = true)
    public void gerarRelatorioCirculacaoDiaria() {
        List<FuncionarioProprio> funcionarios = funcionarioRepository.findAll();
        List<Terceirizado> terceirizados = terceirizadoRepository.findAll();
        List<Visitante> visitantes = visitanteRepository.findAll();

        System.out.println("=== RELATÓRIO: CIRCULAÇÃO DIÁRIA ===");
        funcionarios.forEach(f ->
                System.out.printf("[Funcionario] %s | Status: Interno%n", f.getNome()));

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

    // ---------------- CSV ----------------

    /**
     * CSV da circulação diária.
     * Colunas: Tipo,Nome,Documento,Status,Hora_Entrada
     * Funcionário Próprio entra como "Funcionario", status "Interno" e Hora_Entrada vazia.
     */
    @Transactional(readOnly = true)
    public String exportarCirculacaoDiariaCSV() {
        var funcionarios = funcionarioRepository.findAll();
        var terceirizados = terceirizadoRepository.findAll();
        var visitantes = visitanteRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Tipo,Nome,Documento,Status,Hora_Entrada\n");

        // Funcionários Próprios (sempre internos)
        funcionarios.forEach(f -> {
            sb.append("Funcionario").append(',')
              .append(escapeCsv(f.getNome())).append(',')
              .append(escapeCsv(f.getCpf())).append(',')
              .append("Interno").append(',')
              .append("").append('\n');
        });

        // Terceirizados
        terceirizados.forEach(t -> {
            String status = (t.getDataHoraEntrada() != null && t.getDataHoraSaida() == null) ? "Dentro" : "Fora";
            String hora = t.getDataHoraEntrada() != null ? t.getDataHoraEntrada().toString() : "";
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

    // ---------------- PDF (OpenPDF) ----------------

    /**
     * Gera um PDF simples com título e tabela (OpenPDF).
     * A tabela tem as mesmas colunas do CSV.
     */
    @Transactional(readOnly = true)
    public byte[] exportarRelatorioPDF(String titulo) {
        var funcionarios = funcionarioRepository.findAll();
        var terceirizados = terceirizadoRepository.findAll();
        var visitantes = visitanteRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document();

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Título
            doc.add(new Paragraph(titulo, titleFont));
            doc.add(new Paragraph(" "));

            // Tabela
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100f);
            table.setWidths(new float[]{18f, 28f, 22f, 16f, 16f});

            addHeader(table, "Tipo", headerFont);
            addHeader(table, "Nome", headerFont);
            addHeader(table, "Documento", headerFont);
            addHeader(table, "Status", headerFont);
            addHeader(table, "Hora_Entrada", headerFont);

            // Funcionários Próprios
            for (FuncionarioProprio f : funcionarios) {
                addCell(table, "Funcionario", cellFont);
                addCell(table, f.getNome(), cellFont);
                addCell(table, f.getCpf(), cellFont);
                addCell(table, "Interno", cellFont);
                addCell(table, "", cellFont);
            }

            // Terceirizados
            for (Terceirizado t : terceirizados) {
                String status = (t.getDataHoraEntrada() != null && t.getDataHoraSaida() == null) ? "Dentro" : "Fora";
                String hora = t.getDataHoraEntrada() != null ? t.getDataHoraEntrada().toString() : "";
                addCell(table, "Terceirizado", cellFont);
                addCell(table, t.getNome(), cellFont);
                addCell(table, t.getCpf(), cellFont);
                addCell(table, status, cellFont);
                addCell(table, hora, cellFont);
            }

            // Visitantes
            for (Visitante v : visitantes) {
                String status = (v.getDataHoraEntrada() != null && v.getDataHoraSaida() == null) ? "Dentro" : "Fora";
                String hora = v.getDataHoraEntrada() != null ? v.getDataHoraEntrada().toString() : "";
                addCell(table, "Visitante", cellFont);
                addCell(table, v.getNomeCompleto(), cellFont);
                addCell(table, v.getDocumentoIdentificacao(), cellFont);
                addCell(table, status, cellFont);
                addCell(table, hora, cellFont);
            }

            doc.add(table);
        } catch (DocumentException e) {
            // em caso de erro, devolve um "PDF" textual explicando
            return ("Falha ao gerar PDF: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        } finally {
            doc.close();
        }

        return out.toByteArray();
    }

    private void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5f);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", font));
        cell.setPadding(4f);
        table.addCell(cell);
    }
}