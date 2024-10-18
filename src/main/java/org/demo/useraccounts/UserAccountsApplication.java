package org.demo.useraccounts;

import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.repository.UserAccountRepository;
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
    public CommandLineRunner init(UserAccountRepository repository) {

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
