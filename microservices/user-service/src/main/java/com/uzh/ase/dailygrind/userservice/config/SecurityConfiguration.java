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

/**
 * Security configuration for the application when the "dev" profile is not active.
 * <p>
 * This configuration defines the security settings for the application, including CSRF and session management.
 * It also specifies authentication for certain API endpoints and permits access to Swagger UI and API documentation.
 * </p>
 */
@Configuration
@Profile("!dev")  // Applies this configuration only when the 'dev' profile is not active
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)  // Enables security only if 'security.enabled' is true or missing
@EnableWebSecurity  // Enables Spring Security configuration
public class SecurityConfiguration {

    /**
     * Configures security settings for the application.
     * <p>
     * This method disables CSRF, configures stateless session management, and sets up authentication requirements.
     * It also allows public access to Swagger UI and API documentation while protecting other endpoints.
     * </p>
     *
     * @param http the HttpSecurity instance for configuring HTTP security
     * @return the SecurityFilterChain instance with the configured security settings
     * @throws Exception if an error occurs during the security configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(httpSecurityCorsConfigurer -> {})  // Disables cross-origin request handling
            .csrf(AbstractHttpConfigurer::disable)  // Disables CSRF protection
            .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))  // Returns HTTP 403 when authentication is required
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))  // Configures stateless session management (no server-side session)
            .authorizeHttpRequests(r ->
                r.requestMatchers("/users/**").authenticated()  // Requires authentication for /users/** endpoints
                    .requestMatchers("swagger-ui/**", "/v3/api-docs/**").permitAll()  // Allows public access to Swagger UI and API docs
            )
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));  // Configures OAuth2 resource server with JWT support

        return http.build();  // Builds and returns the SecurityFilterChain instance
    }
}
