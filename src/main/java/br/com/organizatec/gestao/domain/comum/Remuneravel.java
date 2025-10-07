package br.com.organizatec.gestao.domain.comum;

/**
 * Contrato para entidades que possuem remuneração calculável.
 * Demonstra uso de Interface/Polimorfismo na avaliação.
 */
public interface Remuneravel {
    double calcularSalarioTotal();
}