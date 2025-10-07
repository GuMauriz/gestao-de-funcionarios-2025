package br.com.organizatec.gestao.domain.funcionarios;

import java.time.LocalDate;

import br.com.organizatec.gestao.domain.comum.Pessoa;
import br.com.organizatec.gestao.domain.comum.Remuneravel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "funcionarios_proprios")
public class FuncionarioProprio extends Pessoa implements Remuneravel {

    @Column(nullable = false, unique = true, length = 20)
    private String matricula; // será gerada automaticamente em regra futura

    @Column(nullable = false, length = 80)
    private String cargo;

    @Column(name = "salario_base", nullable = false)
    private double salarioBase;

    @Column(name = "data_contratacao", nullable = false)
    private LocalDate dataContratacao;

    @Column(length = 80)
    private String departamento;

    protected FuncionarioProprio() {
        // JPA
    }

    public FuncionarioProprio(String nome, String cpf, LocalDate dataNascimento,
                              String matricula, String cargo, double salarioBase,
                              LocalDate dataContratacao, String departamento) {
        super(nome, cpf, dataNascimento);
        this.matricula = matricula;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.dataContratacao = dataContratacao;
        this.departamento = departamento;
    }

    // Getters/Setters
    public String getMatricula() {
        return matricula;
    }
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public double getSalarioBase() {
        return salarioBase;
    }
    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    public LocalDate getDataContratacao() {
        return dataContratacao;
    }
    public void setDataContratacao(LocalDate dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    @Override
    public double calcularSalarioTotal() {
        if (cargo != null && cargo.equalsIgnoreCase("Gerente")) {
            return this.salarioBase * 1.20; // +20% de bônus
        }
        return this.salarioBase;
    }

    // equals/hashCode podem especializar se necessário; aqui herdamos de Pessoa
}