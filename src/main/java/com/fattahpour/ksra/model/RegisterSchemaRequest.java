package com.fattahpour.ksra.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Payload for registering a new schema with the registry.
 */
public record RegisterSchemaRequest(
        @Schema(description = "Avro schema as JSON string")
        String schema) {
}

