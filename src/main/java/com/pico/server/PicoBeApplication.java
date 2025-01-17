package com.pico.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PicoBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PicoBeApplication.class, args);
    }

}
