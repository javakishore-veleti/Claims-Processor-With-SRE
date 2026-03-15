package com.healthcare.claims.api.tenants.controller;

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
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@AutoConfigureMockMvc
@ActiveProfiles("local")
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthShouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void shouldListTenants() throws Exception {
        mockMvc.perform(get("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetActiveTenants() throws Exception {
        mockMvc.perform(get("/api/v1/tenants/active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateTenant() throws Exception {
        String tenantJson = """
            {
                "tenantId": "TNT-99901",
                "name": "Test Tenant",
                "displayName": "Test Tenant Display",
                "domain": "test-tenant.com",
                "plan": "starter",
                "contactEmail": "admin@test-tenant.com",
                "maxUsers": 10
            }
            """;
        mockMvc.perform(post("/api/v1/tenants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tenantJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.data.name").value("Test Tenant"));
    }
}
