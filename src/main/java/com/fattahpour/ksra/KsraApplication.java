package com.fattahpour.ksra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Kafka Schema Registry Analytics application.
 */
@SpringBootApplication
public class KsraApplication {

    public static void main(String[] args) {
        SpringApplication.run(KsraApplication.class, args);
    }
}
