package com.autohire.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for AutoHire-Flow.
 * Enables component scanning, aspect-oriented programming, and async processing.
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
public class AutoHireFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(AutoHireFlowApplication.class, args);
    }

}
