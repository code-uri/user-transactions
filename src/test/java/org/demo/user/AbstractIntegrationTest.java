package org.demo.user;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MySQLContainer;

import static org.demo.testcontainer.MySQLContainersConfiguration.mySQLContainer;

//@Import(MySQLContainersConfiguration.class)
public class AbstractIntegrationTest {

    static MySQLContainer<?> container = mySQLContainer();

    @BeforeAll
    static void startContainers() {
        container.start();
    }

    @AfterAll
    static void stopContainers() {
        container.stop();
    }
}
