package com.casino.slot.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI slotCasinoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Slot Casino API")
                        .description("REST API dla Slot Casino")
                        .version("1.0.0"));
    }
}