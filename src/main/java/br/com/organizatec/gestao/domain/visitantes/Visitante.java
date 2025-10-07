package br.com.organizatec.gestao.domain.visitantes;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "visitantes")
public class Visitante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nomeCompleto;

    @Column(nullable = false, length = 30)
    private String documentoIdentificacao; // RG, CNH, etc.

    @Column(length = 120)
    private String destino; // pessoa/departamento de destino

    @Column(length = 200)
    private String motivo;

    @Column(name = "entrada", nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Column(name = "saida")
    private LocalDateTime dataHoraSaida;

    @Column(length = 30)
    private String numeroCracha;

    protected Visitante() {
        // JPA
    }

    public Visitante(String nomeCompleto, String documentoIdentificacao, String destino,
                     String motivo, LocalDateTime dataHoraEntrada, String numeroCracha) {
        this.nomeCompleto = nomeCompleto;
        this.documentoIdentificacao = documentoIdentificacao;
        this.destino = destino;
        this.motivo = motivo;
        this.dataHoraEntrada = dataHoraEntrada;
        this.numeroCracha = numeroCracha;
    }

    // Getters/Setters
    public Long getId() { return id; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public String getDocumentoIdentificacao() { return documentoIdentificacao; }
    public void setDocumentoIdentificacao(String documentoIdentificacao) { this.documentoIdentificacao = documentoIdentificacao; }

    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) { this.dataHoraEntrada = dataHoraEntrada; }

    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
    public void setDataHoraSaida(LocalDateTime dataHoraSaida) { this.dataHoraSaida = dataHoraSaida; }

    public String getNumeroCracha() { return numeroCracha; }
    public void setNumeroCracha(String numeroCracha) { this.numeroCracha = numeroCracha; }

    // equals/hashCode por id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Visitante)) return false;
        Visitante v = (Visitante) o;
        return id != null && id.equals(v.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}