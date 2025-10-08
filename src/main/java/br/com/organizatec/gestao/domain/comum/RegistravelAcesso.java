package br.com.organizatec.gestao.domain.comum;

import java.time.LocalDateTime;

/**
 * Contrato para entidades que participam do controle de acesso
 * (entrada e saída) das dependências da Organizatec.
 *
 * <p>Implementações típicas:</p>
 * <ul>
 *   <li>{@code Terceirizado}</li>
 *   <li>{@code Visitante}</li>
 * </ul>
 *
 * <p>As implementações devem manter os campos de auditoria (ex.: data/hora de
 * entrada e saída) de acordo com as validações aqui descritas.</p>
 *
 * @author
 *   Organizatec - Equipe de Desenvolvimento
 * @since 1.0
 */
public interface RegistravelAcesso {

    /**
     * Registra a data/hora de entrada no sistema de acesso.
     *
     * @param hora instante da entrada; não deve ser {@code null}
     * @throws IllegalArgumentException se {@code hora} for {@code null}
     */
    void registrarEntrada(LocalDateTime hora);

    /**
     * Registra a data/hora de saída no sistema de acesso.
     *
     * <p>Regras mínimas recomendadas:</p>
     * <ul>
     *   <li>A entrada deve ter sido previamente registrada;</li>
     *   <li>A saída deve ser posterior à entrada;</li>
     *   <li>{@code hora} não deve ser {@code null}.</li>
     * </ul>
     *
     * @param hora instante da saída; não deve ser {@code null}
     * @throws IllegalStateException se a entrada não tiver sido registrada
     * @throws IllegalArgumentException se {@code hora} for {@code null} ou anterior/igual à entrada
     */
    void registrarSaida(LocalDateTime hora);
}
