package br.com.organizatec.gestao.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;
import br.com.organizatec.gestao.repository.FuncionarioProprioRepository;

@ExtendWith(MockitoExtension.class)
class FuncionarioServiceTest {

    @Mock
    FuncionarioProprioRepository repository;

    @InjectMocks
    FuncionarioService service;

    @Test
    @DisplayName("Deve gerar matrícula automaticamente antes de salvar")
    void deveGerarMatriculaAutomaticamente() {
        // dado que não existe CPF igual
        when(repository.findByCpf(anyString())).thenReturn(Optional.empty());

        int ano = Year.now().getValue();
        String prefixo = "MAT-" + ano + "-";

        // simula última matrícula do ano: MAT-YYYY-0009
        FuncionarioProprio ultimo = new FuncionarioProprio(
                "Ultimo", "000.000.000-00", LocalDate.of(1990,1,1),
                prefixo + "0009", "Analista", 3000.0, LocalDate.of(2024,1,1), "TI"
        );
        when(repository.findTopByMatriculaStartingWithOrderByMatriculaDesc(prefixo))
                .thenReturn(Optional.of(ultimo));

        // quando salvar, apenas retorne o objeto que recebeu (simulação do JPA)
        when(repository.save(any(FuncionarioProprio.class)))
                .thenAnswer(inv -> inv.getArgument(0, FuncionarioProprio.class));

        FuncionarioProprio novo = new FuncionarioProprio(
                "Ana", "123.456.789-00", LocalDate.of(1995,2,10),
                null, "Analista", 4000.0, LocalDate.of(2024,1,15), "TI"
        );

        FuncionarioProprio salvo = service.criarFuncionario(novo);

        // captura para checar matrícula gerada
        ArgumentCaptor<FuncionarioProprio> captor = ArgumentCaptor.forClass(FuncionarioProprio.class);
        verify(repository).save(captor.capture());

        String matriculaGerada = captor.getValue().getMatricula();
        assertThat(matriculaGerada).isEqualTo(prefixo + "0010"); // 0009 -> 0010
        assertThat(salvo.getMatricula()).isEqualTo(prefixo + "0010");
    }

    @Test
    @DisplayName("Deve calcular salário polimorficamente (Gerente 20% a mais; Analista sem bônus)")
    void deveCalcularSalarioPolimorficamente() {
        // gerente
        FuncionarioProprio gerente = new FuncionarioProprio(
                "Carlos", "111.222.333-44", LocalDate.of(1990,5,20),
                "X", "Gerente", 1000.0, LocalDate.of(2024,1,10), "TI"
        );
        assertThat(gerente.calcularSalarioTotal()).isEqualTo(1200.0);

        // analista
        FuncionarioProprio analista = new FuncionarioProprio(
                "Marina", "555.666.777-88", LocalDate.of(1998,12,1),
                "Y", "Analista", 1000.0, LocalDate.of(2023,6,1), "RH"
        );
        assertThat(analista.calcularSalarioTotal()).isEqualTo(1000.0);
    }
}