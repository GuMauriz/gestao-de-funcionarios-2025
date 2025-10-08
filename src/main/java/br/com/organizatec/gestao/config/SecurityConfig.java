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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.com.organizatec.gestao.config.logging.MdcLoggingFilter;

@Configuration
public class SecurityConfig {

    private static final AntPathRequestMatcher H2_MATCHER =
            new AntPathRequestMatcher("/h2-console/**");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   MdcLoggingFilter mdcLoggingFilter) throws Exception {

        http
            .csrf(csrf -> csrf.ignoringRequestMatchers(H2_MATCHER).disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                // H2 (somente RH; em prod remover)
                .requestMatchers(H2_MATCHER).hasRole("RH")

                // Funcionários (exato e recursivo)
                .requestMatchers("/funcionarios", "/funcionarios/**").hasRole("RH")

                // Terceirizados
                .requestMatchers("/terceirizados", "/terceirizados/**").hasRole("RH")

                // Visitantes
                .requestMatchers("/visitantes", "/visitantes/**").hasRole("RECEPCAO")

                // Acesso (entrada/saída)
                .requestMatchers("/acesso", "/acesso/**").hasAnyRole("RECEPCAO", "SEGURANCA")

                // Relatórios
                .requestMatchers("/relatorios", "/relatorios/**").hasAnyRole("RH", "SEGURANCA")

                // Tudo o resto precisa estar logado (não libera sem papel)
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
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