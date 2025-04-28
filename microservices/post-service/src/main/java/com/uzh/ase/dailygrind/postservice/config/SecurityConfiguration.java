package com.uzh.ase.dailygrind.userservice.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@Profile("!dev")
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(httpSecurityCorsConfigurer -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(r ->
                r.requestMatchers("/users/**").authenticated()
                    .requestMatchers("swagger-ui/**", "/v3/api-docs/**").permitAll())
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
