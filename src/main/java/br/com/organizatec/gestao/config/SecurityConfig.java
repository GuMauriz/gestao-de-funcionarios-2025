package br.com.organizatec.gestao.config;

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

/**
 * Configuração de segurança estável e compatível.
 * - Libera /funcionarios e /acesso sem autenticação.
 * - Protege /relatorios/** para ROLE_RH.
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desativa CSRF (facilita testes e requisições via Postman)
            .csrf(csrf -> csrf.disable())

            // Define as regras de autorização
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/relatorios/**").hasRole("RH")   // só RH acessa relatórios
                .requestMatchers("/funcionarios/**", "/acesso/**").permitAll() // libera APIs de funcionários e controle de acesso
                .anyRequest().permitAll()
            )

            // Usa autenticação HTTP Basic (para endpoints protegidos)
            .httpBasic(Customizer.withDefaults());

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