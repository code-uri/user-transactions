package org.demo.useraccounts;

import io.asyncer.r2dbc.mysql.MySqlConnectionFactoryProvider;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.ZoneId;

@OpenAPIDefinition
@SpringBootApplication
@Slf4j
public class UserAccountsApplication {

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer mysqlCustomizer() {
        return (builder) ->
                builder.option(MySqlConnectionFactoryProvider.SERVER_ZONE_ID, ZoneId.of(
                        "UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(UserAccountsApplication.class, args);
    }
}
