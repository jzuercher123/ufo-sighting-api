package com.ufomap.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class UfoSightingApiApplication {

    private static final Logger logger = LoggerFactory.getLogger(UfoSightingApiApplication.class);

    public static void main(String[] args)
    {
        try {
            logger.info("Starting UfoSightingApiApplication...");
            SpringApplication.run(UfoSightingApiApplication.class, args);
            logger.info("UfoSightingApiApplication started successfully.");
        } catch (Exception e) {
            logger.error("Error starting UfoSightingApiApplication: {}", e.getMessage(), e);
            throw e; // Rethrow the exception to indicate failure
        }

        logger.info("UfoSightingApiApplication is running.");
    }
}