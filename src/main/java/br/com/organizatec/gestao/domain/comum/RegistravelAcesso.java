package br.com.organizatec.gestao.domain.comum;

import java.time.LocalDateTime;

/**
 * Abstração para entidades que participam de controle de acesso.
 * Implementações devem registrar entrada e saída.
 */
public interface RegistravelAcesso {

    void registrarEntrada(LocalDateTime hora);

    void registrarSaida(LocalDateTime hora);
}