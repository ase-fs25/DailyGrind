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
 * Security configuration class for the development profile.
 * <p>
 * This configuration disables most security features for easier local development and testing:
 * <ul>
 *   <li>Disables CSRF protection</li>
 *   <li>Permits all incoming requests without authentication</li>
 *   <li>Sets session management to stateless</li>
 *   <li>Defines a custom authentication entry point that returns 403 Forbidden</li>
 *   <li>Enables OAuth2 JWT support (though not enforced due to `permitAll`)</li>
 * </ul>
 * <p>
 * This configuration is activated when the property {@code security.enabled} is set to {@code false}.
 * A warning will be logged indicating that security is disabled under the current profile.
 * </p>
 */
@Configuration
@ConditionalOnProperty(name = "security.enabled", havingValue = "false")
@EnableWebSecurity
@Slf4j
public class DevSecurityConfig {

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This configuration is intended for development use and disables most security features:
     * <ul>
     *   <li>Disables CSRF protection</li>
     *   <li>Permits all incoming requests without authentication</li>
     *   <li>Sets session management to stateless</li>
     *   <li>Defines a custom authentication entry point that returns 403 Forbidden</li>
     *   <li>Enables OAuth2 JWT support (though not enforced due to `permitAll`)</li>
     * </ul>
     * <p>
     * Logs a warning indicating that security is disabled under the current profile.
     * </p>
     *
     * @param http the {@link HttpSecurity} to configure
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs during security configuration
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
