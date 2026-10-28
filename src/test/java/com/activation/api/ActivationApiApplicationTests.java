package com.activation.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest(properties = "scheduler.enabled=false")
@Import(TestcontainersConfiguration.class)
class ActivationApiApplicationTests {

    @Test
    void contextLoads() {
    }
}
