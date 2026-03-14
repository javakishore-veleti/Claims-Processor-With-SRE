package com.healthcare.claims.portal.advisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.healthcare.claims.common")
public class PortalClaimsAdvisorApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalClaimsAdvisorApplication.class, args);
    }
}
