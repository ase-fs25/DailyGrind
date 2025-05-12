package com.uzh.ase.dailygrind.pushnotificationsservice.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS) settings in the application.
 * <p>
 * This class implements {@link WebMvcConfigurer} and overrides the {@link #addCorsMappings(CorsRegistry)} method
 * to configure CORS settings for the application. It reads CORS configuration properties from application properties
 * and logs the allowed origins and methods. If no allowed origins are configured, an error is logged.
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    /**
     * List of allowed origins for CORS requests.
     * <p>
     * These origins are read from the application properties file.
     */
    @Value("${dg.us.cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * List of allowed methods for CORS requests.
     * <p>
     * These methods are read from the application properties file.
     */
    @Value("${dg.us.cors.allowed-methods}")
    private String[] allowedMethods;

    /**
     * Configures the CORS settings for the application.
     * <p>
     * This method sets up allowed origins and methods for CORS requests. It also logs the configured values for
     * debugging purposes. If no allowed origins are configured, it logs an error message.
     *
     * @param registry the {@link CorsRegistry} used to add CORS mappings.
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Check if allowed origins are configured
        if (allowedOrigins == null || allowedOrigins.length == 0) {
            log.error("List of allowed CORS origins is not configured!");
            return;
        }

        // Log allowed CORS configurations for debugging
        log.info("Allowed CORS origins: {}", String.join(", ", allowedOrigins));
        log.info("Allowed CORS methods: {}", String.join(", ", allowedMethods));

        // Add mappings for CORS requests
        registry.addMapping("/**").allowedOrigins(allowedOrigins);
        registry.addMapping("/**").allowedMethods(allowedMethods);
    }
}
