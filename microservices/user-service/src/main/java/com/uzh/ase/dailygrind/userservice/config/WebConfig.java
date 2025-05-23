package com.uzh.ase.dailygrind.userservice.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class that configures Cross-Origin Resource Sharing (CORS) settings for the application.
 * <p>
 * This class configures the allowed CORS origins and HTTP methods for the application. It logs the configured values
 * to ensure proper monitoring. If the allowed origins or methods are not set correctly, an error is logged.
 */
@Configuration
@Slf4j  // Lombok annotation for logging
public class WebConfig implements WebMvcConfigurer {

    /**
     * List of allowed origins for CORS.
     */
    @Value("${dg.us.cors.allowed-origins}")
    private String[] allowedOrigins;

    /**
     * List of allowed HTTP methods for CORS.
     */
    @Value("${dg.us.cors.allowed-methods}")
    private String[] allowedMethods;

    /**
     * Configures CORS mappings for the application.
     * <p>
     * This method adds mappings to allow cross-origin requests from specified origins
     * and methods, as configured in the application properties. If the allowed origins
     * are not configured, an error is logged.
     * </p>
     *
     * @param registry the CORS registry to register the mappings
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        // Check if allowed origins are properly configured
        if (allowedOrigins == null || allowedOrigins.length == 0) {
            log.error("List allowed CORS origins is not configured!");
            return;
        }

        // Log the configured CORS settings
        log.info("Allowed CORS origins: {}", String.join(", ", allowedOrigins));
        log.info("Allowed CORS methods: {}", String.join(", ", allowedMethods));

        // Configure the CORS mappings
        registry.addMapping("/**").allowedOrigins(allowedOrigins);
        registry.addMapping("/**").allowedMethods(allowedMethods);
    }
}
