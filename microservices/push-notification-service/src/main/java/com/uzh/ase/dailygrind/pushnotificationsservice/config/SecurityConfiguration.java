package com.uzh.ase.dailygrind.pushnotificationsservice.config;

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
 * Security configuration for the Push Notifications service.
 * <p>
 * This class configures security settings for the Push Notifications service using Spring Security.
 * It disables CSRF protection, configures stateless session management, handles authentication
 * entry points, and defines the URL paths that are accessible without authentication (e.g., for push notifications and Swagger UI).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This method customizes the security settings for the application:
     * <ul>
     *     <li>Disables CSRF protection for stateless authentication (common for REST APIs).</li>
     *     <li>Configures exception handling to return HTTP 403 (Forbidden) for authentication failures.</li>
     *     <li>Sets session creation policy to stateless to prevent the use of HTTP sessions.</li>
     *     <li>Permits access to certain URL patterns (e.g., push notifications, Swagger UI, and API docs) without authentication.</li>
     *     <li>Enables OAuth 2.0 Resource Server with JWT authentication for secured endpoints.</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} to configure the security filter chain.
     * @return the configured {@link SecurityFilterChain} for the application.
     * @throws Exception if any error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(httpSecurityCorsConfigurer -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(r -> r
                .requestMatchers("/push-notifications/**").permitAll()  // Allow unauthenticated access to push notifications.
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll())  // Allow unauthenticated access to Swagger UI.
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));  // Enable JWT authentication for OAuth2 Resource Server.

        return http.build();
    }
}
