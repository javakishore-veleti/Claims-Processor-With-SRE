package com.healthcare.claims.api.claims.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class ClaimsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void swaggerUiShouldBeAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void getClaimsShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void createClaimShouldReturnCreated() throws Exception {
        String claimJson = """
            {
                "tenantId": "default-tenant",
                "customerId": "CUST-001",
                "claimNumber": "CLM-TEST-001",
                "stage": "INTAKE_RECEIVED"
            }
            """;
        mockMvc.perform(post("/api/v1/claims")
                .contentType(MediaType.APPLICATION_JSON)
                .content(claimJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.data.claimNumber").value("CLM-TEST-001"));
    }
}
