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
 * Security configuration class that configures HTTP security for the application.
 * <p>
 * This class sets up various security settings for the application, including disabling CSRF protection,
 * enabling OAuth2 resource server support with JWT authentication, configuring CORS, and setting up session
 * management to stateless mode. Additionally, it sets up custom exception handling and access control rules.
 */
@Configuration
@Profile("!dev")
@ConditionalOnProperty(name = "security.enabled", havingValue = "true", matchIfMissing = true)
@EnableWebSecurity
public class SecurityConfiguration {

    /**
     * Configures the HTTP security for the application.
     * <p>
     * This method configures various security features such as CORS, CSRF protection, session management,
     * exception handling, and authorization rules for different endpoints. Specifically, it configures the following:
     * <ul>
     *   <li>Disables CSRF protection</li>
     *   <li>Sets the session management to stateless (for REST APIs)</li>
     *   <li>Configures custom exception handling for forbidden requests</li>
     *   <li>Requires authentication for `/users/**` and `/posts/**` endpoints</li>
     *   <li>Allows unauthenticated access to Swagger UI and API documentation</li>
     *   <li>Enables OAuth2 resource server support using JWT for authentication</li>
     * </ul>
     *
     * @param http the {@link HttpSecurity} object used to configure security settings
     * @return the configured {@link SecurityFilterChain} instance
     * @throws Exception if an error occurs during configuration
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
                    .requestMatchers("swagger-ui/**", "/v3/api-docs/**").permitAll()
                    .requestMatchers("actuator/**").permitAll())
            .oauth2ResourceServer(s -> s.jwt(Customizer.withDefaults()));

        return http.build();
    }
}
