package br.com.organizatec.gestao.repository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.organizatec.gestao.domain.funcionarios.FuncionarioProprio;

/**
 * Teste focado na camada de persistência (H2 em memória).
 * Valida geração de ID e salvamento de campos herdados (Pessoa) e específicos.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
class FuncionarioProprioRepositoryTest {

    @Autowired
    private FuncionarioProprioRepository repository;

    @Test
    @DisplayName("Deve salvar FuncionarioProprio e gerar ID (persistência básica)")
    void deveSalvarFuncionarioEGerarId() {
        // arrange
        FuncionarioProprio f = new FuncionarioProprio(
                "Carlos Silva",
                "111.222.333-44",
                LocalDate.of(1990, 5, 20),
                "MAT-TEST-001",
                "Desenvolvedor",
                5000.00,
                LocalDate.of(2024, 1, 10),
                "TI"
        );

        // act
        FuncionarioProprio salvo = repository.save(f);

        // assert
        assertThat(salvo.getId()).as("ID gerado pelo JPA").isNotNull();
    }

    @Test
    @DisplayName("Deve salvar todos os campos herdados e específicos (herança Pessoa + FuncionarioProprio)")
    void deveSalvarCamposHerdadosEEspecificos() {
        // arrange
        FuncionarioProprio f = new FuncionarioProprio(
                "Marina Costa",
                "555.666.777-88",
                LocalDate.of(1998, 12, 1),
                "MAT-TEST-002",
                "Analista",
                4200.00,
                LocalDate.of(2023, 6, 1),
                "RH"
        );

        // act
        FuncionarioProprio salvo = repository.save(f);
        Long id = salvo.getId();

        // reload
        FuncionarioProprio encontrado = repository.findById(id).orElseThrow();

        // assert herdados (Pessoa)
        assertThat(encontrado.getNome()).isEqualTo("Marina Costa");
        assertThat(encontrado.getCpf()).isEqualTo("555.666.777-88");
        assertThat(encontrado.getDataNascimento()).isEqualTo(LocalDate.of(1998, 12, 1));

        // assert específicos
        assertThat(encontrado.getMatricula()).isEqualTo("MAT-TEST-002");
        assertThat(encontrado.getCargo()).isEqualTo("Analista");
        assertThat(encontrado.getSalarioBase()).isEqualTo(4200.00);
        assertThat(encontrado.getDataContratacao()).isEqualTo(LocalDate.of(2023, 6, 1));
        assertThat(encontrado.getDepartamento()).isEqualTo("RH");
    }
}