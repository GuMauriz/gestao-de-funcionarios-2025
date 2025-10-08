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

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(h -> h.frameOptions(frame -> frame.sameOrigin())) // se usar H2 console
            .authorizeHttpRequests(auth -> auth
                // Relatórios (RH e Segurança)
                .requestMatchers("/relatorios/**").hasAnyRole("RH","SEGURANCA")

                // CRUDs já existentes na API
                .requestMatchers("/funcionarios/**").hasRole("RH")
                .requestMatchers("/terceirizados/**").hasRole("RH")
                .requestMatchers("/visitantes/**").hasRole("RECEPCAO")
                .requestMatchers("/acesso/**").hasAnyRole("RECEPCAO","SEGURANCA")

                // Telas web de cadastro
                .requestMatchers("/cadastro/funcionario","/cadastro/terceirizado").hasRole("RH")
                .requestMatchers("/cadastro/visitante").hasRole("RECEPCAO")

                // H2 console (opcional: restrinja a RH ou desligue em produção)
                .requestMatchers("/h2/**").hasRole("RH")

                // dashboard e estáticos
                .requestMatchers("/", "/home", "/css/**", "/js/**", "/images/**").authenticated()

                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails rh = User.withUsername("rh").password(encoder.encode("rh123")).roles("RH").build();
        UserDetails recepcao = User.withUsername("recepcao").password(encoder.encode("recepcao123")).roles("RECEPCAO").build();
        UserDetails seguranca = User.withUsername("seguranca").password(encoder.encode("seg123")).roles("SEGURANCA").build();
        return new InMemoryUserDetailsManager(rh, recepcao, seguranca);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}