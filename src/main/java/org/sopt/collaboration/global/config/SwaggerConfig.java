package org.sopt.collaboration.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("사람인 리디자인 API")
                        .description("SOPT 39기 합동세미나 사람인 UX/UI 리디자인 백엔드 API")
                        .version("v1"));
    }
}
