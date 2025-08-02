package com.fattahpour.ksra.client;

import com.fattahpour.ksra.model.SchemaVersionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Reactive client for interacting with a Kafka Schema Registry.
 */
@Component
public class SchemaRegistryClient {

    private final WebClient webClient;

    public SchemaRegistryClient(@Value("${schema.registry.url}") String baseUrl, WebClient.Builder builder) {
        this.webClient = builder.baseUrl(baseUrl).build();
    }

    /**
     * Fetch all registered subjects from the schema registry.
     *
     * @return a Flux stream of subject names
     */
    public Flux<String> getSubjects() {
        return webClient.get()
                .uri("/subjects")
                .retrieve()
                .bodyToFlux(String.class);
    }

    /**
     * Fetch all versions for the given subject.
     *
     * @param subject the subject name
     * @return a Flux stream of version numbers
     */
    public Flux<Integer> getVersions(String subject) {
        return webClient.get()
                .uri("/subjects/{subject}/versions", subject)
                .retrieve()
                .bodyToFlux(Integer.class);
    }

    /**
     * Fetch a particular schema version for a subject.
     *
     * @param subject the subject name
     * @param version the version number
     * @return the schema information
     */
    public Mono<SchemaVersionResponse> getSchema(String subject, int version) {
        return webClient.get()
                .uri("/subjects/{subject}/versions/{version}", subject, version)
                .retrieve()
                .bodyToMono(SchemaVersionResponse.class);
    }
}
