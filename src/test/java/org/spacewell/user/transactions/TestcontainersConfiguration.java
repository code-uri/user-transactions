package org.spacewell.user.transactions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {


    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer();
    }


//    @Container
//    @ServiceConnection
//    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer();
}
