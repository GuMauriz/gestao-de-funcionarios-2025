package br.com.organizatec.gestao.service;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;

/**
 * Serviço de Relatórios (CSV / PDF) e saída em console.
 * Ajustado para usar os getters reais de FuncionarioProprio, Terceirizado e Visitante.
 */
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
     * Imprime no console um relatório de circulação (apenas para debug/apresentação).
     */
    public void gerarRelatorioCirculacaoDiaria() {
        List<FuncionarioProprio> funcionarios = funcionarioRepository.findAll();
        List<Terceirizado> terceirizados = terceirizadoRepository.findAll();
        List<Visitante> visitantes = visitanteRepository.findAll();

        System.out.println("RELATÓRIO: CIRCULAÇÃO DIÁRIA");
        for (FuncionarioProprio f : funcionarios) {
            System.out.printf("[FuncionarioProprio] %s | CPF: %s | Status: %s%n",
                    f.getNome(), f.getCpf(), "Interno");
        }
        for (Terceirizado t : terceirizados) {
            String status = statusAcesso(t.getDataHoraEntrada(), t.getDataHoraSaida());
            System.out.printf("[Terceirizado] %s | CPF: %s | Status: %s%n",
                    t.getNome(), t.getCpf(), status);
        }
        for (Visitante v : visitantes) {
            String status = statusAcesso(v.getDataHoraEntrada(), v.getDataHoraSaida());
            System.out.printf("[Visitante] %s | DOC: %s | Status: %s%n",
                    v.getNome(), v.getCpf(), status); // usando cpf herdado de Pessoa
        }
    }

    private String statusAcesso(LocalDateTime entrada, LocalDateTime saida) {
        if (entrada != null && (saida == null || saida.isBefore(entrada))) {
            return "Dentro";
        }
        return "Fora";
    }

    /**
     * Exporta a circulação diária para CSV (texto).
     * Cabeçalho: Tipo,Nome,Documento,Status,Hora_Entrada
     */
    public String exportarCirculacaoDiariaCSV() {
        List<FuncionarioProprio> funcionarios = funcionarioRepository.findAll();
        List<Terceirizado> terceirizados = terceirizadoRepository.findAll();
        List<Visitante> visitantes = visitanteRepository.findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("Tipo,Nome,Documento,Status,Hora_Entrada\n");

        for (FuncionarioProprio f : funcionarios) {
            sb.append("FuncionarioProprio").append(',')
              .append(escapeCsv(f.getNome())).append(',')
              .append(escapeCsv(f.getCpf())).append(',')
              .append("Interno").append(',')
              .append("").append('\n');
        }
        for (Terceirizado t : terceirizados) {
            sb.append("Terceirizado").append(',')
              .append(escapeCsv(t.getNome())).append(',')
              .append(escapeCsv(t.getCpf())).append(',')
              .append(statusAcesso(t.getDataHoraEntrada(), t.getDataHoraSaida())).append(',')
              .append(t.getDataHoraEntrada() != null ? t.getDataHoraEntrada() : "").append('\n');
        }
        for (Visitante v : visitantes) {
            sb.append("Visitante").append(',')
              .append(escapeCsv(v.getNome())).append(',')
              .append(escapeCsv(v.getCpf())).append(',')
              .append(statusAcesso(v.getDataHoraEntrada(), v.getDataHoraSaida())).append(',')
              .append(v.getDataHoraEntrada() != null ? v.getDataHoraEntrada() : "").append('\n');
        }
        return sb.toString();
    }

    private String escapeCsv(String s) {
        if (s == null) return "";
        boolean hasSep = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String value = s.replace("\"", "\"\"");
        return hasSep ? "\"" + value + "\"" : value;
    }

    /**
     * Gera um PDF simples (OpenPDF) com título e tabela de circulação.
     */
    public byte[] exportarRelatorioPDF(String titulo) {
        List<FuncionarioProprio> funcionarios = funcionarioRepository.findAll();
        List<Terceirizado> terceirizados = terceirizadoRepository.findAll();
        List<Visitante> visitantes = visitanteRepository.findAll();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font h1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            doc.add(new Paragraph(titulo, h1));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100f);

            headerCell(table, "Tipo");
            headerCell(table, "Nome");
            headerCell(table, "Documento");
            headerCell(table, "Status");
            headerCell(table, "Hora_Entrada");

            for (FuncionarioProprio f : funcionarios) {
                rowCell(table, "FuncionarioProprio");
                rowCell(table, nvl(f.getNome()));
                rowCell(table, nvl(f.getCpf()));
                rowCell(table, "Interno");
                rowCell(table, "");
            }
            for (Terceirizado t : terceirizados) {
                rowCell(table, "Terceirizado");
                rowCell(table, nvl(t.getNome()));
                rowCell(table, nvl(t.getCpf()));
                rowCell(table, statusAcesso(t.getDataHoraEntrada(), t.getDataHoraSaida()));
                rowCell(table, t.getDataHoraEntrada() != null ? t.getDataHoraEntrada().toString() : "");
            }
            for (Visitante v : visitantes) {
                rowCell(table, "Visitante");
                rowCell(table, nvl(v.getNome()));
                rowCell(table, nvl(v.getCpf()));
                rowCell(table, statusAcesso(v.getDataHoraEntrada(), v.getDataHoraSaida()));
                rowCell(table, v.getDataHoraEntrada() != null ? v.getDataHoraEntrada().toString() : "");
            }

            doc.add(table);
        } catch (DocumentException e) {
            return ("ERRO AO GERAR PDF: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        } finally {
            doc.close();
        }
        return baos.toByteArray();
    }

    private void headerCell(PdfPTable table, String text) {
        Font f = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        PdfPCell c = new PdfPCell(new Phrase(text, f));
        table.addCell(c);
    }

    private void rowCell(PdfPTable table, String text) {
        PdfPCell c = new PdfPCell(new Phrase(text));
        table.addCell(c);
    }

    private String nvl(String s) { return s == null ? "" : s; }
}