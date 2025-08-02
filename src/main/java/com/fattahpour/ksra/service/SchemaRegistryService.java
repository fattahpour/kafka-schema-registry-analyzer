package com.fattahpour.ksra.service;

import com.fattahpour.ksra.client.SchemaRegistryClient;
import com.fattahpour.ksra.model.RegisterSchemaRequest;
import com.fattahpour.ksra.model.RegisterSchemaResponse;
import com.fattahpour.ksra.model.SchemaDiff;
import com.fattahpour.ksra.model.SchemaStringResponse;
import com.fattahpour.ksra.model.SchemaVersionResponse;
import com.fattahpour.ksra.util.SchemaDiffUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Business logic for working with schemas from the registry.
 */
@Service
public class SchemaRegistryService {

    private final SchemaRegistryClient client;

    public SchemaRegistryService(SchemaRegistryClient client) {
        this.client = client;
    }

    /**
     * @return all subjects registered in the schema registry
     */
    public Flux<String> getSubjects() {
        return client.getSubjects();
    }

    /**
     * Fetch all versions for the given subject.
     *
     * @param subject the subject name
     * @return a Flux stream of versions
     */
    public Flux<Integer> getVersions(String subject) {
        return client.getVersions(subject);
    }

    /**
     * Retrieve a schema by its global id.
     *
     * @param id schema id
     * @return schema definition as string
     */
    public Mono<String> getSchemaById(int id) {
        return client.getSchemaById(id).map(SchemaStringResponse::schema);
    }

    /**
     * Register a schema under the specified subject.
     *
     * @param subject subject name
     * @param schema  schema definition
     * @return assigned schema id
     */
    public Mono<Integer> registerSchema(String subject, String schema) {
        RegisterSchemaRequest request = new RegisterSchemaRequest(schema);
        return client.registerSchema(subject, request).map(RegisterSchemaResponse::id);
    }

    /**
     * Compute the diff between two versions of a subject.
     *
     * @param subject subject name
     * @param v1 first version
     * @param v2 second version
     * @return differences between the schemas
     */
    public Mono<SchemaDiff> diff(String subject, int v1, int v2) {
        Mono<String> s1 = client.getSchema(subject, v1).map(SchemaVersionResponse::schema);
        Mono<String> s2 = client.getSchema(subject, v2).map(SchemaVersionResponse::schema);

        return Mono.zip(s1, s2)
                .map(tuple -> {
                    try {
                        return SchemaDiffUtil.diff(tuple.getT1(), tuple.getT2());
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Failed to diff schemas", e);
                    }
                });
    }
}
