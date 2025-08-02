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
    void openApiSpecAvailable() {
        webTestClient.get().uri("/v3/api-docs")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.info.title").isEqualTo("Kafka Schema Registry Analyzer API");
    }
}

