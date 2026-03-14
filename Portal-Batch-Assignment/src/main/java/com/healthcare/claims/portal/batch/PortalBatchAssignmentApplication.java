package com.healthcare.claims.portal.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableBatchProcessing
@EnableFeignClients(basePackages = "com.healthcare.claims.common")
public class PortalBatchAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalBatchAssignmentApplication.class, args);
    }
}
