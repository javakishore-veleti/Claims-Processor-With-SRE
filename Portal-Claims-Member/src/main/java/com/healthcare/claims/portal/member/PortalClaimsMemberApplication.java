package com.healthcare.claims.portal.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.healthcare.claims.common")
public class PortalClaimsMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalClaimsMemberApplication.class, args);
    }
}
