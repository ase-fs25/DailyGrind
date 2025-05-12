package com.uzh.ase.dailygrind.postservice.config;

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
 * Security configuration class that defines the security filter chain for the application.
 * <p>
 * The configuration applies when the application is running in any profile except "dev"
 * (due to the {@link Profile} annotation) and when the "security.enabled" property
 * is set to true (or is not defined, as per the default value).
 * <p>
 * The following security settings are applied:
 * - CORS support is enabled with an empty configuration.
 * - CSRF protection is disabled (for stateless APIs).
 * - All requests to "/users/**" and "/posts/**" require authentication.
 * - The Swagger UI and API docs are publicly accessible without authentication.
 * - The session creation policy is set to stateless (no sessions).
 * - OAuth2 resource server is configured to use JWT for authentication.
 *
 * @see HttpSecurity
 * @see SecurityFilterChain
 */
@Configuration
@Profile("!dev")
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Configures the security filter chain to handle various authentication and authorization settings.
     *
     * @param http the {@link HttpSecurity} object to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during the security configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(httpSecurityCorsConfigurer -> {})
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(c -> c.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.FORBIDDEN)))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(r ->
                r.requestMatchers("/users/**").authenticated()
                    .requestMatchers("/posts/**").authenticated()
                    .requestMatchers("swagger-ui/**", "/v3/api-docs/**").permitAll())
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
