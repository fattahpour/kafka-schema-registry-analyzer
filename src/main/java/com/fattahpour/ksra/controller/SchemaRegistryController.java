package com.fattahpour.ksra.controller;

import com.fattahpour.ksra.model.RegisterSchemaRequest;
import com.fattahpour.ksra.service.SchemaRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * WebFlux controller exposing basic Schema Registry operations.
 */
@RestController
@Tag(name = "Schema Registry")
public class SchemaRegistryController {

    private final SchemaRegistryService service;
    private final WebClient webClient;

    public SchemaRegistryController(SchemaRegistryService service, WebClient.Builder builder,
                                    @Value("${schema.registry.url:http://localhost:8081}") String baseUrl) {
        this.service = service;
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    @Operation(summary = "List all schema subjects")
    @ApiResponse(responseCode = "200", description = "Array of subject names")
    @GetMapping("/subjects")
    public Flux<String> listSubjects() {
        return service.getSubjects();
    }

    @Operation(summary = "List versions of a subject")
    @ApiResponse(responseCode = "200", description = "Array of version numbers")
    @GetMapping("/subjects/{subject}/versions")
    public Flux<Integer> listVersions(
            @Parameter(description = "Subject name")
            @PathVariable String subject) {
        return service.getVersions(subject);
    }

    @Operation(summary = "Fetch schema by global ID")
    @ApiResponse(responseCode = "200", description = "Schema string")
    @GetMapping("/schemas/ids/{id}")
    public Mono<String> getSchemaById(
            @Parameter(description = "Global schema id")
            @PathVariable int id) {
        return service.getSchemaById(id);
    }

    @Operation(summary = "Register a new Avro schema under a subject")
    @ApiResponse(responseCode = "200", description = "Assigned schema id",
            content = @Content(schema = @Schema(implementation = Integer.class)))
    @PostMapping(value = "/subjects/{subject}/versions", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Integer> registerSchema(
            @Parameter(description = "Subject name")
            @PathVariable String subject,
            @RequestBody RegisterSchemaRequest request) {
        return service.registerSchema(subject, request.schema());
    }

    @Operation(summary = "Get summary for a schema registry subject")
    @ApiResponse(responseCode = "200", description = "Subject summary")
    @GetMapping("/api/schema-registry/summary/{subject}")
    public Flux<Map<String, Object>> schemaRegistrySummary(
            @Parameter(description = "Subject name")
            @PathVariable String subject) {
        return Mono.zip(
                webClient.get()
                        .uri("/subjects/{subject}/versions", Map.of("subject", subject))
                        .retrieve()
                        .bodyToFlux(Integer.class)
                        .collectList(),
                webClient.get()
                        .uri("/subjects/{subject}/versions/latest", Map.of("subject", subject))
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
        ).map(tuple -> {
            Map<String, Object> latest = tuple.getT2();
            Map<String, Object> result = new HashMap<>();
            result.put("subject", subject);
            result.put("topic", subject.replaceAll("-(value|key)$", ""));
            result.put("versions", tuple.getT1());
            result.put("schemaId", latest.get("id"));
            result.put("schema", latest.get("schema"));
            return result;
        }).flux();
    }
}

