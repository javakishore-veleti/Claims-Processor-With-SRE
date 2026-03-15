package com.healthcare.claims.api.members.controller;

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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldListMembers() throws Exception {
        mockMvc.perform(get("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateMember() throws Exception {
        String memberJson = """
            {
                "tenantId": "default-tenant",
                "memberId": "MBR-TEST-001",
                "firstName": "John",
                "lastName": "Doe",
                "dateOfBirth": "1990-01-15",
                "email": "john.doe@test.com",
                "phone": "555-1234",
                "policyNumber": "POL-001",
                "policyStatus": "ACTIVE"
            }
            """;
        mockMvc.perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(memberJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }
}
