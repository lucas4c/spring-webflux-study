package com.drago.webflux_api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Padrão ouro da indústria
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable()) // Desabilitado pois usamos JWT (Stateless)
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .authorizeExchange(exchanges -> exchanges
                // Criar usuário e Login serão públicos
                .pathMatchers(HttpMethod.POST, "/api/users").permitAll()
                .pathMatchers("/api/auth/**").permitAll()
                // Todo o resto exige token válido
                .anyExchange().authenticated()
            )
            .addFilterAt(jwtAuthenticationFilter, org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}