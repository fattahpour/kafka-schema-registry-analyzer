package com.fattahpour.ksra.controller;

import com.fattahpour.ksra.model.SchemaDiff;
import com.fattahpour.ksra.service.SchemaRegistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST endpoints for interacting with schema subjects and versions.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Subject Management")
public class SubjectController {

    private final SchemaRegistryService service;

    public SubjectController(SchemaRegistryService service) {
        this.service = service;
    }

    @Operation(summary = "List all schema subjects")
    @ApiResponse(responseCode = "200", description = "Array of subject names")
    @GetMapping("/subjects")
    public Flux<String> getSubjects() {
        return service.getSubjects();
    }

    @Operation(summary = "List versions of a subject")
    @ApiResponse(responseCode = "200", description = "Array of version numbers")
    @GetMapping("/subjects/{subject}/versions")
    public Flux<Integer> getVersions(
            @Parameter(description = "Subject name")
            @PathVariable String subject) {
        return service.getVersions(subject);
    }

    @Operation(summary = "Diff two schema versions")
    @ApiResponse(responseCode = "200", description = "Schema diff result")
    @GetMapping("/subjects/{subject}/versions/{v1}/diff/{v2}")
    public Mono<SchemaDiff> diff(
            @Parameter(description = "Subject name") @PathVariable String subject,
            @Parameter(description = "First version") @PathVariable int v1,
            @Parameter(description = "Second version") @PathVariable int v2) {
        return service.diff(subject, v1, v2);
    }
}
