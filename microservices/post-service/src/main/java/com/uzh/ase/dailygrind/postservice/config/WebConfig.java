package com.uzh.ase.dailygrind.postservice.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to handle Cross-Origin Resource Sharing (CORS) settings for the application.
 * <p>
 * This class configures the allowed origins and allowed methods for CORS based on values
 * from the application's configuration properties. It logs the configured CORS settings
 * and applies them to all incoming requests.
 * </p>
 * <p>
 * The CORS configuration will only be applied if the allowed origins are properly set
 * in the application's configuration. Otherwise, an error message will be logged.
 * </p>
 *
 * @see CorsRegistry
 */
@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${dg.us.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${dg.us.cors.allowed-methods}")
    private String[] allowedMethods;

    /**
     * Configures the CORS settings for the application.
     * <p>
     * This method will configure the allowed origins and methods for all incoming requests
     * based on the values configured in the application's properties. It will also log
     * the applied settings.
     * </p>
     *
     * @param registry the {@link CorsRegistry} used to configure the CORS settings
     */
    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        if (allowedOrigins == null || allowedOrigins.length == 0) {
            log.error("List of allowed CORS origins is not configured!");
            return;
        }

        log.info("Allowed CORS origins: {}", String.join(", ", allowedOrigins));
        log.info("Allowed CORS methods: {}", String.join(", ", allowedMethods));

        registry.addMapping("/**").allowedOrigins(allowedOrigins);
        registry.addMapping("/**").allowedMethods(allowedMethods);
    }
}
