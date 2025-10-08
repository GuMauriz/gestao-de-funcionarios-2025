package br.com.organizatec.gestao.config;

import br.com.organizatec.gestao.config.logging.MdcLoggingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    private static final AntPathRequestMatcher H2_MATCHER =
            new AntPathRequestMatcher("/h2-console/**");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   MdcLoggingFilter mdcLoggingFilter) throws Exception {

        http
            // CSRF desabilitado para API; H2-console explicitamente ignorado
            .csrf(csrf -> csrf.ignoringRequestMatchers(H2_MATCHER).disable())

            // Necessário para o H2 abrir em frame
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // Autorização por perfil, conforme a política da Organizatec
            .authorizeHttpRequests(auth -> auth
                // H2 console: somente RH (em prod: ideal remover/ desabilitar)
                .requestMatchers(H2_MATCHER).hasRole("RH")

                // CRUD Funcionários Próprios: RH
                .requestMatchers("/funcionarios/**").hasRole("RH")

                // CRUD Terceirizados: RH
                .requestMatchers("/terceirizados/**").hasRole("RH")

                // CRUD Visitantes: RECEPCAO
                .requestMatchers("/visitantes/**").hasRole("RECEPCAO")

                // Controle de acesso (entrada/saída): RECEPCAO e SEGURANCA
                .requestMatchers("/acesso/**").hasAnyRole("RECEPCAO", "SEGURANCA")

                // Relatórios: RH e SEGURANCA
                .requestMatchers("/relatorios/**").hasAnyRole("RH", "SEGURANCA")

                // Qualquer outra rota não mapeada: exigir autenticação
                .anyRequest().authenticated()
            )

            // HTTP Basic para facilitar testes/entrega
            .httpBasic(Customizer.withDefaults())

            // Filtro de auditoria (MDC) antes do filtro de autenticação
            .addFilterBefore(mdcLoggingFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails rh = User.withUsername("rh")
                .password(encoder.encode("rh123"))
                .roles("RH")
                .build();

        UserDetails recepcao = User.withUsername("recepcao")
                .password(encoder.encode("recepcao123"))
                .roles("RECEPCAO")
                .build();

        UserDetails seguranca = User.withUsername("seguranca")
                .password(encoder.encode("seg123"))
                .roles("SEGURANCA")
                .build();

        return new InMemoryUserDetailsManager(rh, recepcao, seguranca);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}