package org.demo.useraccounts;


import org.demo.useraccounts.model.UserAccount;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Hooks;
import reactor.test.StepVerifier;

import java.util.Arrays;


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = InfrastructureConfiguration.class)
@AutoConfigureWebTestClient(timeout = "36000")
@TestPropertySource( properties = {
        "spring.r2dbc.url=r2dbc:tc:mysql:///databasename?TC_IMAGE_TAG=8.0.36",
        "spring.r2dbc.username=user",
        "spring.r2dbc.password=password",
        "spring.sql.init.mode=ALWAYS",
        "logging.level.org.springframework.r2dbc.core=DEBUG",
        "logging.level.reactor.netty.http.client=DEBUG",
        "junit.jupiter.execution.parallel.enabled=false"
})
public class UserAccountRoutesTests {

    @Autowired
    DatabaseClient database;

    @BeforeEach
    void setUp() {

        Hooks.onOperatorDebug();

        var statements = Arrays.asList(//
                "DROP TABLE IF EXISTS user_accounts;",
                """
                            CREATE TABLE IF NOT EXISTS user_accounts (
                            id SERIAL PRIMARY KEY PRIMARY KEY,
                            first_name VARCHAR(50) NOT NULL,
                            last_name VARCHAR(50) NOT NULL,
                            balance DOUBLE NOT NULL DEFAULT 0,
                            currency VARCHAR(4) NOT NULL DEFAULT 'EUR',
                            status VARCHAR(24) NOT NULL DEFAULT 'ACTIVE',
                            created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            modified_on TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);
                        """,
                "DROP TABLE IF EXISTS transactions;",
                """
                          CREATE TABLE IF NOT EXISTS transactions (
                              id SERIAL PRIMARY KEY PRIMARY KEY,
                             user_account_id BIGINT NOT NULL,
                             txn_type VARCHAR(24) NOT NULL,
                             amount DOUBLE NOT NULL DEFAULT 0,
                             balance DOUBLE NOT NULL DEFAULT 0,
                             currency VARCHAR(4) NOT NULL,
                             status VARCHAR(24),
                             original_transaction_id BIGINT,
                             original_transaction_type VARCHAR(24) ,
                             created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             modified_on TIMESTAMP ON UPDATE CURRENT_TIMESTAMP);
                        """);


        statements.forEach(it -> database.sql(it)//
                .fetch() //
                .rowsUpdated() //
                .as(StepVerifier::create) //
                .expectNextCount(1) //
                .verifyComplete());
    }


    @Test
    void test_user_account_validation_constraints(@Autowired WebTestClient webClient) {

        UserAccount userAccount =
                UserAccount.builder()
                        .firstName("").lastName("smith").balance(-1D).currency("EUR").build();

        createUserAccountExchange(webClient, userAccount)
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    Assertions.assertNotNull(problemDetail.getDetail());
                    Assertions.assertTrue(problemDetail.getDetail().contains("field 'firstName': rejected"));
                    Assertions.assertTrue(problemDetail.getDetail().contains("field 'balance': rejected"));
                    Assertions.assertFalse(problemDetail.getDetail().contains("field 'lastName': rejected"));
                    Assertions.assertFalse(problemDetail.getDetail().contains("field 'currency': rejected"));
                });
    }

    @Test
    void test_create_user_account_success(@Autowired WebTestClient webClient) {

        UserAccount userAccount =
                UserAccount.builder()
                        .firstName("john").lastName("smith").balance(0D).currency("EUR").build();

        createUserAccountExchange(webClient, userAccount)
                .expectBody(UserAccount.class)
                .value(account -> {
                    Assertions.assertEquals("john", account.getFirstName());
                    Assertions.assertEquals(0, account.getBalance());
                });
    }



    @Test
    void test_user_account_find_by_id_success(@Autowired WebTestClient webClient) {

        UserAccount userAccount =
                UserAccount.builder()
                        .firstName("john").lastName("smith").balance(0D).currency("EUR").build();

        UserAccount userAccount1 = createUserAccountExchange(webClient, userAccount)
                .expectStatus().is2xxSuccessful()
                .expectBody(UserAccount.class).returnResult().getResponseBody();
        fineByIDExchange(webClient, userAccount1.getId()).expectStatus().is2xxSuccessful();
    }

    @Test
    void test_update_user_account_success(@Autowired WebTestClient webClient) {

        UserAccount userAccount =
                UserAccount.builder()
                        .firstName("john").lastName("smith").balance(0D).currency("EUR").build();


        createUserAccountExchange(webClient, userAccount)
                .expectStatus().is2xxSuccessful();

        userAccount =
                UserAccount.builder()
                        .id(1L)
                        .firstName("john").lastName("koduri").balance(0D).currency("EUR").build();


        updateUserAccountExchange(webClient, userAccount)
                .expectBody(UserAccount.class)
                .value(updated -> {
                   Assertions.assertEquals("koduri", updated.getLastName());
                });
    }


    @Test
    void test_suspend_user_account_success(@Autowired WebTestClient webClient) {

        UserAccount userAccount =
                UserAccount.builder()
                        .firstName("john").lastName("smith").balance(0D).currency("EUR").build();

        userAccount = createUserAccountExchange(webClient, userAccount)
                .expectBody(UserAccount.class).returnResult().getResponseBody();

        System.out.println("suspend account "+userAccount.getId());
        suspendByIDExchange(webClient, userAccount.getId())
                .expectStatus().is2xxSuccessful();

        userAccount = fineByIDExchange(webClient, userAccount.getId())
                .expectBody(UserAccount.class).returnResult().getResponseBody();

       Assertions.assertEquals(UserAccount.UserAccountStatus.SUSPENDED, userAccount.getStatus());

    }



    private static WebTestClient.@NotNull ResponseSpec createUserAccountExchange(WebTestClient webClient, UserAccount userAccount) {
        return webClient
                .post().uri("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userAccount)
                .exchange();
    }

    private static WebTestClient.@NotNull ResponseSpec updateUserAccountExchange(WebTestClient webClient, UserAccount userAccount) {
        return webClient
                .put().uri("/accounts/"+userAccount.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(userAccount)
                . exchange();
    }


    private static WebTestClient.@NotNull ResponseSpec fineByIDExchange(WebTestClient webClient, Long id) {
        return webClient
                .get().uri("/accounts/" + id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    private static WebTestClient.@NotNull ResponseSpec suspendByIDExchange(WebTestClient webClient, Long id) {
        return webClient
                .delete().uri("/accounts/"+id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

}
