package org.demo.testcontainer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
public class MySQLContainersConfiguration {


    @Bean
    @ServiceConnection
    public static MySQLContainer<?> mySQLContainer() {
        return new MySQLContainer<>("mysql:8.0.36")
                .withDatabaseName("testdb")
                .withInitScript("init_mysql.sql")
                .withUsername("root")
                .withPassword("secret")
                .withUrlParam("logger", "com.mysql.cj.log.Slf4JLogger")
                .withUrlParam("profileSQL", "true");
    }
}
