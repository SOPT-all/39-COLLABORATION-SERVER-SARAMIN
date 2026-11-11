package org.sopt.collaboration.global.config;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final String[] allowedOriginPatterns;

    public CorsConfig(@Value("${app.cors.allowed-origins}") String[] allowedOrigins) {
        this.allowedOriginPatterns = Arrays.stream(allowedOrigins)
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        // 비어 있으면 CorsRegistry 기본값(모든 출처 허용)이 적용되므로 기동을 막는다
        if (this.allowedOriginPatterns.length == 0) {
            throw new IllegalStateException("app.cors.allowed-origins 가 비어 있습니다.");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
