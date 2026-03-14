package com.healthcare.claims.portal.entitlements;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.healthcare.claims.common")
public class PortalEntitlementsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalEntitlementsApplication.class, args);
    }
}
