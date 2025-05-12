package com.uzh.ase.dailygrind.postservice.config;

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
@Slf4j
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
     * Adds CORS mappings to allow specific origins and HTTP methods for cross-origin requests.
     * <p>
     * If the allowed origins or methods are not configured, an error is logged. Otherwise, the allowed origins and
     * methods are logged, and CORS mappings are added to allow cross-origin requests from those origins with the
     * specified methods.
     *
     * @param registry the {@link CorsRegistry} used to register CORS mappings
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        if (allowedOrigins == null || allowedOrigins.length == 0) {
            log.error("List allowed CORS origins is not configured!");
            return;
        }

        log.info("Allowed CORS origins: {}", String.join(", ", allowedOrigins));
        log.info("Allowed CORS methods: {}", String.join(", ", allowedMethods));

        registry.addMapping("/**").allowedOrigins(allowedOrigins);
        registry.addMapping("/**").allowedMethods(allowedMethods);
    }
}
