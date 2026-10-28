package com.activation.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ActivationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ActivationApiApplication.class, args);
    }

}
