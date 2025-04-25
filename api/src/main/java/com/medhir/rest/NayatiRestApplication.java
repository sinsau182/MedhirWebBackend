package com.medhir.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class NayatiRestApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NayatiRestApplication.class);
        app.run(args);
    }
}
