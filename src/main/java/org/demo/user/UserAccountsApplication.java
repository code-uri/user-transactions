package org.demo.user;

import lombok.extern.slf4j.Slf4j;
import org.demo.user.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@SpringBootApplication
@Slf4j
public class UserAccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserAccountsApplication.class, args);
    }

    @Bean
    public CommandLineRunner init(CustomerRepository repository) {

        return (args) -> {
            repository.findAll().doOnNext(customer -> {
                        log.info("--------------------------------");
                        log.info(customer.toString());
                        log.info("");
                    })
                    .blockLast(Duration.ofSeconds(10));
        };
    }
}
