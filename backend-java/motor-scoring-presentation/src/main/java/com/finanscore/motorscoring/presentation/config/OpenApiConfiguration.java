package com.finanscore.motorscoring.presentation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI openApi() {
        return new OpenAPI().info(new Info()
                .title("Motor de Scoring Crediticio API")
                .version("2.0.2")
                .description("RF04, RF05 y RF06 con Arquitectura Hexagonal. MongoDB es el adaptador de salida activo por defecto y H2/JPA permanece disponible mediante el profile h2."));
    }
}
