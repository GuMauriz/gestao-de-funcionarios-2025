package br.com.organizatec.gestao.domain.visitantes;

import java.time.LocalDateTime;

import br.com.organizatec.gestao.domain.comum.Pessoa;
import br.com.organizatec.gestao.domain.comum.RegistravelAcesso;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade de Visitante.
 */
@Entity
@Table(name = "visitantes")
public class Visitante extends Pessoa implements RegistravelAcesso {

    private String empresaOrigem;
    private String motivoVisita;

    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;

    /** Construtor exigido pelo JPA */
    public Visitante() {
        super();
    }

    /** Construtor de conveniência (dataNascimento deixada como null) */
    public Visitante(String nome, String cpf, String empresaOrigem, String motivoVisita) {
        super(nome, cpf, null);
        this.empresaOrigem = empresaOrigem;
        this.motivoVisita = motivoVisita;
    }

    // ==== Controle de acesso ====
    @Override
    public void registrarEntrada(LocalDateTime hora) {
        this.dataHoraEntrada = hora;
    }

    @Override
    public void registrarSaida(LocalDateTime hora) {
        if (this.dataHoraEntrada == null) {
            throw new IllegalStateException("Entrada não registrada antes da saída.");
        }
        this.dataHoraSaida = hora;
    }

    // ==== Getters/Setters ====
    public String getEmpresaOrigem() { return empresaOrigem; }
    public void setEmpresaOrigem(String empresaOrigem) { this.empresaOrigem = empresaOrigem; }

    public String getMotivoVisita() { return motivoVisita; }
    public void setMotivoVisita(String motivoVisita) { this.motivoVisita = motivoVisita; }

    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
}