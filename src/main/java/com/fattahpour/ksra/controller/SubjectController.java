package com.fattahpour.ksra.controller;

import com.fattahpour.ksra.model.SchemaDiff;
import com.fattahpour.ksra.service.SchemaRegistryService;
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
public class SubjectController {

    private final SchemaRegistryService service;

    public SubjectController(SchemaRegistryService service) {
        this.service = service;
    }

    @GetMapping("/subjects")
    public Flux<String> getSubjects() {
        return service.getSubjects();
    }

    @GetMapping("/subjects/{subject}/versions")
    public Flux<Integer> getVersions(@PathVariable String subject) {
        return service.getVersions(subject);
    }

    @GetMapping("/subjects/{subject}/versions/{v1}/diff/{v2}")
    public Mono<SchemaDiff> diff(@PathVariable String subject,
                                 @PathVariable int v1,
                                 @PathVariable int v2) {
        return service.diff(subject, v1, v2);
    }
}
