package com.fattahpour.ksra;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SchemaRegistryControllerTest {

    private static MockWebServer mockWebServer;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        registry.add("schema.registry.url", () -> mockWebServer.url("/").toString());
    }

    @AfterAll
    static void shutdown() throws IOException {
        mockWebServer.shutdown();
    }

    @Autowired
    WebTestClient webTestClient;

    @Test
    void listSubjects() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[\"topic1\",\"topic2\"]")
                .addHeader("Content-Type", "application/json"));

        webTestClient.get().uri("/subjects")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(String.class)
                .contains("topic1", "topic2");
    }

    @Test
    void schemaRegistrySummary() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("[1,2]")
                .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":100,\"schema\":\"{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"Test1\\\"}\"}")
                .addHeader("Content-Type", "application/json"));

        webTestClient.get().uri("/api/schema-registry/summary/topic1-value")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].subject").isEqualTo("topic1-value")
                .jsonPath("$[0].topic").isEqualTo("topic1")
                .jsonPath("$[0].versions[1]").isEqualTo(2)
                .jsonPath("$[0].schemaId").isEqualTo(100)
                .jsonPath("$[0].schema").isEqualTo("{\"type\":\"record\",\"name\":\"Test1\"}");
    }

    @Test
    void diffSchemas() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"subject\":\"test\",\"version\":1,\"id\":1,\"schema\":\"{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"T\\\",\\\"fields\\\":[{\\\"name\\\":\\\"a\\\",\\\"type\\\":\\\"string\\\"}]}\"}")
                .addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"subject\":\"test\",\"version\":2,\"id\":2,\"schema\":\"{\\\"type\\\":\\\"record\\\",\\\"name\\\":\\\"T\\\",\\\"fields\\\":[{\\\"name\\\":\\\"a\\\",\\\"type\\\":\\\"string\\\"},{\\\"name\\\":\\\"b\\\",\\\"type\\\":\\\"int\\\"}]}\"}")
                .addHeader("Content-Type", "application/json"));

        webTestClient.get().uri("/subjects/test/versions/1/diff/2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.addedFields[0]").isEqualTo("b")
                .jsonPath("$.removedFields").isEmpty()
                .jsonPath("$.changedFields").isEmpty();
    }

    @Test
    void openApiSpecAvailable() {
        webTestClient.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.info.title").isEqualTo("Kafka Schema Registry Analyzer API");
    }
}
