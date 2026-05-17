package com.recintos.municipalidad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();}

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource))
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authz -> authz
            .requestMatchers("/api/csrf-token").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS,"/**").permitAll()
            .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/encargados/**").permitAll()
            .requestMatchers("/api/user/**", "/api/event/**", "/api/inscripcion/**", "/api/recinto/**",
                "/api/estadisticas/**", "/ws-recintos/**", "/api/encargados/**", "/api/event/*/assign-manager/**",
                "/api/categoria/**", "/api/storage/**", "/api/curso/**","/uploads/**", "/error")
            .permitAll()
            .anyRequest().authenticated());
    return http.build();
  }
}
