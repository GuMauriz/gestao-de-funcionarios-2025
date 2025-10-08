package br.com.organizatec.gestao.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.BDDMockito.given;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock TerceirizadoRepository terceirizadoRepository;
    @Mock VisitanteRepository visitanteRepository;
    @Mock FuncionarioProprioRepository funcionarioRepository;

    @InjectMocks RelatorioService service;

    @Test
    @DisplayName("CSV deve incluir Funcionário (Interno), Terceirizado e Visitante com status correto")
    void csv_deveListarTodos() {
        // Funcionários próprios (usam dataNascimento real na entidade de vocês)
        FuncionarioProprio f1 = new FuncionarioProprio(
                "Gustavo Mauriz",
                "111.222.333-55",
                LocalDate.of(1992, 7, 10),
                "MAT-2025-0001",
                "Gerente",
                9000.0,
                LocalDate.of(2024, 3, 1),
                "TI"
        );

        // Terceirizado (construtor compatível: nome, cpf, empresaOrigem, setor)
        Terceirizado t1 = new Terceirizado("Alex Saifi", "222.333.444-55", "TechClean", "Limpeza");
        t1.registrarEntrada(LocalDateTime.of(2025, 10, 7, 10, 0)); // dentro (sem saída)

        // Visitante (construtor compatível: nome, cpf, empresaOrigem, motivo)
        Visitante v1 = new Visitante("Nicolas Gomes", "333.444.555-66", "SoftPlus", "Reunião");
        v1.registrarEntrada(LocalDateTime.of(2025, 10, 7, 11, 0));
        v1.registrarSaida(LocalDateTime.of(2025, 10, 7, 12, 0)); // fora (tem saída após entrada)

        given(funcionarioRepository.findAll()).willReturn(List.of(f1));
        given(terceirizadoRepository.findAll()).willReturn(List.of(t1));
        given(visitanteRepository.findAll()).willReturn(List.of(v1));

        String csv = service.exportarCirculacaoDiariaCSV();

        assertThat(csv).startsWith("Tipo,Nome,Documento,Status,Hora_Entrada");
        assertThat(csv).contains("FuncionarioProprio,Gustavo Mauriz,111.222.333-55,Interno,");
        assertThat(csv).contains("Terceirizado,Alex Saifi,222.333.444-55,Dentro,2025-10-07T10:00");
        assertThat(csv).contains("Visitante,Nicolas Gomes,333.444.555-66,Fora,2025-10-07T11:00");
    }

    @Test
    @DisplayName("PDF deve ser gerado (byte[] não vazio) com OpenPDF")
    void pdf_deveSerGerado() {
        FuncionarioProprio f1 = new FuncionarioProprio(
                "Israel Florentino",
                "999.888.777-66",
                LocalDate.of(1994, 9, 12),
                "MAT-2025-0002",
                "Analista",
                4500.0,
                LocalDate.of(2024, 2, 1),
                "RH"
        );

        Terceirizado t1 = new Terceirizado("Maria Sato", "444.555.666-77", "PredialService", "Portaria");
        Visitante v1 = new Visitante("Camila Duarte", "555.666.777-88", "DataX", "Entrevista");

        given(funcionarioRepository.findAll()).willReturn(List.of(f1));
        given(terceirizadoRepository.findAll()).willReturn(List.of(t1));
        given(visitanteRepository.findAll()).willReturn(List.of(v1));

        byte[] pdf = service.exportarRelatorioPDF("Relatório de Circulação Diária");
        assertThat(pdf).isNotNull();
        assertThat(pdf.length).isGreaterThan(100); // heurística mínima
    }
}