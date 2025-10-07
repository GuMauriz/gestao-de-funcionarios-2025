package br.com.organizatec.gestao.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.organizatec.gestao.domain.comum.RegistravelAcesso;
import br.com.organizatec.gestao.domain.terceirizados.Terceirizado;
import br.com.organizatec.gestao.domain.visitantes.Visitante;

class ControleAcessoTest {

    @Test
    @DisplayName("Polimorfismo: registrarEntrada() deve funcionar para Terceirizado e Visitante via interface")
    void polimorfismoRegistrarEntrada() {
        // arrange
        Terceirizado terceirizado = new Terceirizado(
                "Alex Terceiro", "111.111.111-11", LocalDate.of(1985, 1, 1),
                "Eletricista", "Empresa FESA Elétrica",
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31),
                "Nicoles Gomes"
        );

        Visitante visitante = new Visitante(
                "Israel Visitante", "RG123", "TI",
                "Reunião", null, "C-001"
        );

        List<RegistravelAcesso> lista = List.of(terceirizado, visitante);

        LocalDateTime agora = LocalDateTime.now();

        // act - polimórfico (chamamos o mesmo método para tipos diferentes)
        lista.forEach(r -> r.registrarEntrada(agora));

        // assert
        assertThat(terceirizado.getDataHoraEntrada()).isEqualTo(agora);
        assertThat(visitante.getDataHoraEntrada()).isEqualTo(agora);
    }
}