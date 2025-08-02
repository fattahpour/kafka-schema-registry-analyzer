package com.fattahpour.ksra.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a schema version as returned by the Schema Registry.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaVersionResponse {

    private final String subject;
    private final int version;
    private final int id;
    private final String schema;

    public SchemaVersionResponse(@JsonProperty("subject") String subject,
                                 @JsonProperty("version") int version,
                                 @JsonProperty("id") int id,
                                 @JsonProperty("schema") String schema) {
        this.subject = subject;
        this.version = version;
        this.id = id;
        this.schema = schema;
    }

    public String subject() {
        return subject;
    }

    public int version() {
        return version;
    }

    public int id() {
        return id;
    }

    public String schema() {
        return schema;
    }
}
