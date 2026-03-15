package com.healthcare.claims.api.members;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration",
        "spring.cloud.compatibility-verifier.enabled=false"
    }
)
@ActiveProfiles("local")
class ApiMembersApplicationTests {

    @Test
    void contextLoads() {
    }
}
