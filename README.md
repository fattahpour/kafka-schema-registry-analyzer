# Kafka Schema Registry Analyzer

A Spring Boot application for analyzing data in Kafka Schema Registry.

## Prerequisites
- Java 17 or later
- Docker (optional if running in container)

## Run the application
Use the Maven wrapper to start the application:
```bash
./mvnw spring-boot:run
```
The REST API is documented and can be exercised via Swagger UI once the app is running:
```
http://localhost:8080/swagger-ui.html
```

## Test services
Use `curl` or the Swagger UI to interact with the available endpoints. Examples:

List registered subjects:
```bash
curl http://localhost:8080/subjects
```

Register a new schema from a file:
```bash
curl -X POST -H "Content-Type: application/json" \
     -d '{"schema": "$(cat schema.avsc | jq -Rs .)"}' \
     http://localhost:8080/subjects/my-subject/versions
```

## Run tests
Execute the test suite with:
```bash
./mvnw test
```

