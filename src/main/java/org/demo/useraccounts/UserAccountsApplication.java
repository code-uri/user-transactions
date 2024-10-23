package org.demo.useraccounts;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the User Accounts application.
 * This class is responsible for bootstrapping the Spring Boot application.
 */
@OpenAPIDefinition
@SpringBootApplication
@Slf4j
public class UserAccountsApplication {

    /**
     * Main method to run the Spring Boot application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(UserAccountsApplication.class, args);
    }
}