package com.medhir.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NayatiRestApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(NayatiRestApplication.class);
        app.run(args);
    }
}
