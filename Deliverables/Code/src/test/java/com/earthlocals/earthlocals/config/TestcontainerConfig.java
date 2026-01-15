package com.earthlocals.earthlocals.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainerConfig {

    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("defaultdb")
            .withUsername("avnadmin")
            .withPassword("test_pass")
            .withCopyFileToContainer(
                    org.testcontainers.utility.MountableFile.forClasspathResource("schema.sql"),
                    "/docker-entrypoint-initdb.d/schema.sql"
            );

    static {
        postgres.start(); // Starts once when the class is loaded
    }

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }

}
