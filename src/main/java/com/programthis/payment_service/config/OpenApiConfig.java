package com.programthis.payment_service.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Payment Service API",
        version = "1.0.0",
        description = "API para gestionar y procesar pagos."
    )
)
public class OpenApiConfig {
}