package com.uzh.ase.dailygrind.postservice.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfig implements WebMvcConfigurer {

    @Value("${dg.us.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Value("${dg.us.cors.allowed-methods}")
    private String[] allowedMethods;

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
