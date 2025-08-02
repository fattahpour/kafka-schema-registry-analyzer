package com.fattahpour.ksra.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "Kafka Schema Registry Analyzer API",
        version = "1.0.0",
        description = "REST endpoints for interacting with the Kafka Schema Registry"))
public class OpenApiConfig {
}
