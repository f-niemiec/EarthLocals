package com.earthlocals.earthlocals.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainerConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        var container = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("defaultdb")
                .withUsername("avnadmin")
                .withPassword("test_pass");
        container.start(); // Ensure it is running before returning the bean
        return container;
    }

}
