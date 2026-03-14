package com.healthcare.claims.portal.sre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.healthcare.claims.common")
public class PortalSreApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalSreApplication.class, args);
    }
}
