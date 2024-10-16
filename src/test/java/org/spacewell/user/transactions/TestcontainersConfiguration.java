package org.spacewell.user.transactions;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfiguration(proxyBeanMethods = false)
@Testcontainers()
class TestcontainersConfiguration {

    @Container
    @ServiceConnection
    private static final MySQLContainer MY_SQL_CONTAINER = new MySQLContainer();

    // will be started before and stopped after each test method

    @Test
    void test() {
        assertTrue(MY_SQL_CONTAINER.isRunning());
    }

}
