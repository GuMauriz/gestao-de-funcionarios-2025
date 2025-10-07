package br.com.organizatec.gestao.domain.terceirizados;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.organizatec.gestao.domain.comum.Pessoa;
import br.com.organizatec.gestao.domain.comum.RegistravelAcesso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "terceirizados")
public class Terceirizado extends Pessoa implements RegistravelAcesso {

    @Column(nullable = false, length = 80)
    private String funcao;

    @Column(nullable = false, length = 120)
    private String empresa;

    @Column(name = "inicio_contrato", nullable = false)
    private LocalDate inicioContrato;

    @Column(name = "fim_contrato", nullable = false)
    private LocalDate fimContrato;

    @Column(name = "responsavel_interno", length = 120)
    private String responsavelInterno;

    // ---- Controle de Acesso (abstração comum) ----
    @Column(name = "entrada")
    private LocalDateTime dataHoraEntrada;

    @Column(name = "saida")
    private LocalDateTime dataHoraSaida;

    protected Terceirizado() {
        // JPA
    }

    public Terceirizado(String nome, String cpf, LocalDate dataNascimento,
                        String funcao, String empresa,
                        LocalDate inicioContrato, LocalDate fimContrato,
                        String responsavelInterno) {
        super(nome, cpf, dataNascimento);
        this.funcao = funcao;
        this.empresa = empresa;
        this.inicioContrato = inicioContrato;
        this.fimContrato = fimContrato;
        this.responsavelInterno = responsavelInterno;
    }

    // Getters/Setters
    public String getFuncao() { return funcao; }
    public void setFuncao(String funcao) { this.funcao = funcao; }

    public String getEmpresa() { return empresa; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }

    public LocalDate getInicioContrato() { return inicioContrato; }
    public void setInicioContrato(LocalDate inicioContrato) { this.inicioContrato = inicioContrato; }

    public LocalDate getFimContrato() { return fimContrato; }
    public void setFimContrato(LocalDate fimContrato) { this.fimContrato = fimContrato; }

    public String getResponsavelInterno() { return responsavelInterno; }
    public void setResponsavelInterno(String responsavelInterno) { this.responsavelInterno = responsavelInterno; }

    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }

    // ---- Implementação da interface RegistravelAcesso ----
    @Override
    public void registrarEntrada(LocalDateTime hora) {
        this.dataHoraEntrada = hora;
        this.dataHoraSaida = null; // reinicia saída num novo ciclo
    }

    @Override
    public void registrarSaida(LocalDateTime hora) {
        if (this.dataHoraEntrada == null) {
            throw new IllegalStateException("Não é possível registrar saída sem entrada.");
        }
        if (hora.isBefore(this.dataHoraEntrada)) {
            throw new IllegalArgumentException("Saída não pode ser anterior à entrada.");
        }
        this.dataHoraSaida = hora;
    }
}