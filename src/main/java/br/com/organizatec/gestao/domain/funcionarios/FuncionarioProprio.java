package br.com.organizatec.gestao.domain.funcionarios;

import java.time.LocalDate;
import java.util.Objects;

import br.com.organizatec.gestao.domain.comum.Pessoa;
import br.com.organizatec.gestao.domain.comum.Remuneravel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * Representa um colaborador próprio (CLT) da Organizatec.
 *
 * <p>Regras principais:</p>
 * <ul>
 *   <li>Extende {@link Pessoa} e implementa {@link Remuneravel}, permitindo o
 *   cálculo polimórfico de remuneração.</li>
 *   <li>A matrícula é gerada automaticamente na camada de serviço.</li>
 *   <li>O salário total aplica bônus de 20% quando o cargo for "Gerente".</li>
 * </ul>
 *
 * <p><b>Mapeamento JPA:</b> a entidade é persistida na tabela {@code funcionarios_proprios}.</p>
 *
 * @author
 *   Organizatec - Equipe de Desenvolvimento
 * @since 1.0
 */
@Entity
@Table(name = "funcionarios_proprios",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_funcionarios_proprios_cpf", columnNames = {"cpf"}),
           @UniqueConstraint(name = "uk_funcionarios_proprios_matricula", columnNames = {"matricula"})
       })
public class FuncionarioProprio extends Pessoa implements Remuneravel {

    /** Código de matrícula institucional (ex.: {@code MAT-2025-0001}). */
    @Column(length = 20, unique = true)
    private String matricula;

    /** Cargo ocupado pelo funcionário (ex.: Gerente, Analista). */
    @Column(length = 60, nullable = false)
    private String cargo;

    /** Salário base acordado em contrato (sem adicionais). */
    @Column(nullable = false)
    private double salarioBase;

    /** Data de contratação. */
    @Column(nullable = false)
    private LocalDate dataContratacao;

    /** Departamento ao qual o funcionário pertence (ex.: TI, RH). */
    @Column(length = 60, nullable = false)
    private String departamento;

    /** Construtor padrão exigido pelo JPA. */
    public FuncionarioProprio() { }

    /**
     * Constrói um {@code FuncionarioProprio} com todos os atributos.
     *
     * @param nome            nome completo do funcionário
     * @param cpf             CPF (único)
     * @param dataNascimento  data de nascimento
     * @param matricula       matrícula institucional (pode ser {@code null}; o Service gera)
     * @param cargo           cargo (ex.: Gerente, Analista)
     * @param salarioBase     salário base
     * @param dataContratacao data da contratação
     * @param departamento    departamento (ex.: TI)
     */
    public FuncionarioProprio(String nome,
                              String cpf,
                              LocalDate dataNascimento,
                              String matricula,
                              String cargo,
                              double salarioBase,
                              LocalDate dataContratacao,
                              String departamento) {
        super(nome, cpf, dataNascimento);
        this.matricula = matricula;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.dataContratacao = dataContratacao;
        this.departamento = departamento;
    }

    // ===================== Regras de Negócio =====================

    /**
     * Calcula a remuneração total do funcionário.
     *
     * <p>Regra:</p>
     * <ul>
     *   <li>Se o cargo for {@code Gerente}, aplica-se bônus de 20% sobre {@link #salarioBase}.</li>
     *   <li>Para qualquer outro cargo, retorna apenas o {@link #salarioBase}.</li>
     * </ul>
     *
     * @return valor total da remuneração a ser paga
     */
    @Override
    public double calcularSalarioTotal() {
        if ("Gerente".equalsIgnoreCase(this.cargo)) {
            return this.salarioBase * 1.20;
        }
        return this.salarioBase;
    }

    // ===================== Getters/Setters =====================

    /**
     * Obtém a matrícula institucional.
     * @return matrícula (ex.: {@code MAT-2025-0001})
     */
    public String getMatricula() {
        return matricula;
    }

    /**
     * Define a matrícula institucional.
     * <p>Normalmente é gerada pela camada de serviço; use com cautela.</p>
     * @param matricula matrícula no formato definido pela organização
     */
    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    /**
     * Obtém o cargo.
     * @return cargo atual do funcionário
     */
    public String getCargo() {
        return cargo;
    }

    /**
     * Define o cargo.
     * @param cargo cargo a atribuir (ex.: Gerente, Analista)
     */
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    /**
     * Obtém o salário base.
     * @return salário base (sem adicionais)
     */
    public double getSalarioBase() {
        return salarioBase;
    }

    /**
     * Define o salário base.
     * @param salarioBase valor do salário base
     */
    public void setSalarioBase(double salarioBase) {
        this.salarioBase = salarioBase;
    }

    /**
     * Obtém a data de contratação.
     * @return data de contratação
     */
    public LocalDate getDataContratacao() {
        return dataContratacao;
    }

    /**
     * Define a data de contratação.
     * @param dataContratacao data da contratação
     */
    public void setDataContratacao(LocalDate dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    /**
     * Obtém o departamento.
     * @return departamento do funcionário
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * Define o departamento.
     * @param departamento nome do departamento (ex.: TI)
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    // ===================== equals/hashCode =====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FuncionarioProprio that)) return false;
        return Objects.equals(getCpf(), that.getCpf());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCpf());
    }

    @Override
    public String toString() {
        return "FuncionarioProprio{" +
                "id=" + getId() +
                ", nome='" + getNome() + '\'' +
                ", cpf='" + getCpf() + '\'' +
                ", matricula='" + matricula + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salarioBase=" + salarioBase +
                ", dataContratacao=" + dataContratacao +
                ", departamento='" + departamento + '\'' +
                '}';
    }
}