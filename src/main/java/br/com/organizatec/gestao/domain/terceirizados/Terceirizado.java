package br.com.organizatec.gestao.domain.terceirizados;

import java.time.LocalDateTime;

import br.com.organizatec.gestao.domain.comum.Pessoa;
import br.com.organizatec.gestao.domain.comum.RegistravelAcesso;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Entidade de Terceirizado.
 */
@Entity
@Table(name = "terceirizados")
public class Terceirizado extends Pessoa implements RegistravelAcesso {

    private String empresaOrigem;
    private String setor;

    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;

    /** Construtor exigido pelo JPA */
    public Terceirizado() {
        super();
    }

    /** Construtor de conveniência (dataNascimento deixada como null) */
    public Terceirizado(String nome, String cpf, String empresaOrigem, String setor) {
        super(nome, cpf, null);
        this.empresaOrigem = empresaOrigem;
        this.setor = setor;
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

    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }

    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
}