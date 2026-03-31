package com.autohire.flow.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 client configuration
 */
@Configuration
public class S3Config {
    
    @Bean
    public S3Client s3Client() {
        return S3Client.builder().build();
    }
}
