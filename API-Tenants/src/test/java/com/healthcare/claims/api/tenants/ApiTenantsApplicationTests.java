package com.healthcare.claims.api.tenants;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@ActiveProfiles("local")
class ApiTenantsApplicationTests {

    @Test
    void contextLoads() {
    }
}
