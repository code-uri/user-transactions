package org.demo.user;


import org.demo.testcontainer.MySQLContainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertTrue;


@Testcontainers
@SpringBootTest
@Import(MySQLContainersConfiguration.class)
public class UserServiceTests {


    @Autowired
    MySQLContainer<?> container;

    @Test
    void loadContext() {

    }


    @Test
    void test_container_dbuser_access() {
        assertTrue(container.isRunning());
    }
}
