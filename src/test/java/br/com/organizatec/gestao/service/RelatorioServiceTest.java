package br.com.organizatec.gestao.service;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;
import br.com.organizatec.gestao.repository.TerceirizadoRepository;
import br.com.organizatec.gestao.repository.VisitanteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    TerceirizadoRepository terceirizadoRepository;

    @Mock
    VisitanteRepository visitanteRepository;

    @Mock
    FuncionarioProprioRepository funcionarioRepository;

    @InjectMocks
    RelatorioService relatorioService;

    @Test
    @DisplayName("exportarCirculacaoDiariaCSV deve incluir Funcionário Próprio (Interno) + Terceirizado + Visitante")
    void csv_incluiFuncionarioProprioTerceirizadoVisitante() {
        var funcionario = new FuncionarioProprio(
                "Funcionario A",
                "555.666.777-88",
                LocalDate.of(1990, 1, 1),
                "MAT-2025-0001",
                "Analista",
                4000.0,
                LocalDate.of(2024, 1, 10),
                "TI"
        );

        var tDentro = new Terceirizado(
                "Terceiro B", "111.111.111-11", LocalDate.of(1985, 1, 1),
                "Eletricista", "Empresa X",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                "João Interno"
        );
        tDentro.registrarEntrada(LocalDateTime.of(2025, 1, 1, 8, 0));

        var vFora = new Visitante(
                "Visitante C", "RG123", "RH", "Entrega",
                LocalDateTime.of(2025, 1, 1, 9, 0), "C-01"
        );
        vFora.registrarSaida(LocalDateTime.of(2025, 1, 1, 10, 0));

        when(funcionarioRepository.findAll()).thenReturn(List.of(funcionario));
        when(terceirizadoRepository.findAll()).thenReturn(List.of(tDentro));
        when(visitanteRepository.findAll()).thenReturn(List.of(vFora));

        String csv = relatorioService.exportarCirculacaoDiariaCSV();

        // Cabeçalho
        assertThat(csv).contains("Tipo,Nome,Documento,Status,Hora_Entrada");

        // Funcionário Próprio
        assertThat(csv).contains("Funcionario,Funcionario A,555.666.777-88,Interno,");

        // Terceirizado (Dentro)
        assertThat(csv).contains("Terceirizado,Terceiro B,111.111.111-11,Dentro,2025-01-01T08:00");

        // Visitante (Fora)
        assertThat(csv).contains("Visitante,Visitante C,RG123,Fora,2025-01-01T09:00");
    }
}