package com.uzh.ase.dailygrind.postservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * Security configuration for the development environment.
 * <p>
 * This class disables security measures in the development profile by allowing all requests without authentication.
 * It also configures OAuth2 resource server settings with JWT authentication, but only if the profile
 * is not explicitly configured to enable security via the {@code security.enabled} property.
 */
@Configuration
@ConditionalOnProperty(name = "security.enabled", havingValue = "false")
@EnableWebSecurity
@Slf4j
public class DevSecurityConfig {

    /**
     * Configures HTTP security for the development profile.
     * <p>
     * This method configures HTTP security to allow all requests without authentication in the development environment.
     * Additionally, it disables CSRF protection and configures JWT authentication for OAuth2 resource servers.
     * A warning is logged to notify that security is disabled in the dev profile.
     *
     * @param http the {@link HttpSecurity} to customize
     * @return a configured {@link SecurityFilterChain} for the development environment
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.warn("=== [Dev Profile] Security is DISABLED: All requests are permitted without authentication. ===");

        http
            .cors(httpSecurityCorsConfigurer -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(r ->
                r.anyRequest().permitAll())
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
