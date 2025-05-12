package com.uzh.ase.dailygrind.userservice.config;

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
 * Configures security settings for the application in the development profile.
 * <p>
 * This configuration disables authentication and permits all incoming requests
 * without requiring authentication when the security.enabled property is set to false.
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "security.enabled", havingValue = "false")
@EnableWebSecurity
@Slf4j
public class DevSecurityConfig {

    /**
     * Configures the HTTP security settings for the development profile.
     * <p>
     * In the development profile, security is disabled, allowing unrestricted access to all endpoints.
     * It also disables CSRF protection and configures JWT-based OAuth2 resource server handling.
     * </p>
     *
     * @param http the HttpSecurity object used to configure security settings
     * @return the configured SecurityFilterChain
     * @throws Exception if there is an error in the configuration process
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.warn("=== [Dev Profile] Security is DISABLED: All requests are permitted without authentication. ===");

        http
            .cors(httpSecurityCorsConfigurer -> {}) // Disable CORS configuration
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
            .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN))) // Handle authentication errors
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Use stateless sessions
            .authorizeHttpRequests(r -> r.anyRequest().permitAll()) // Permit all requests
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults())); // Enable JWT-based OAuth2 resource server

        return http.build(); // Return the configured SecurityFilterChain
    }
}
