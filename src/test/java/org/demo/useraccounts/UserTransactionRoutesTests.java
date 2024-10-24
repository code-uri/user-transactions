package org.demo.useraccounts;


import org.demo.useraccounts.dto.DateRange;
import org.demo.useraccounts.dto.TransactionRequest;
import org.demo.useraccounts.dto.TransactionResponse;
import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
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
public class UserTransactionRoutesTests {

    private static final String FIELD_REJECTED = "field '%s': rejected";
    @Autowired
    DatabaseClient database;

    @Autowired
    UserAccountRepository userAccountRepository;


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
    void test_transaction_zero_debit_fail(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(1D).currency("EUR").build());

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().amount(0D).txnType(Transaction.TxnType.DEBIT).build())
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    Assertions.assertTrue(problemDetail.getDetail().contains(FIELD_REJECTED.formatted("amount")));
                });
    }

    @Test
    void test_transaction_negative_debit_fail(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(1D).currency("EUR").build());

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().amount(-1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    Assertions.assertTrue(problemDetail.getDetail().contains(FIELD_REJECTED.formatted("amount")));
                });
    }


    @Test
    void test_transaction_null_txn_type_fail(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(1D).currency("EUR").build());

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().amount(10D).build())
                .expectBody(ProblemDetail.class)
                .value(problemDetail -> {
                    Assertions.assertTrue(problemDetail.getDetail().contains(FIELD_REJECTED.formatted("txnType")));
                });
    }

    @Test
    void test_rollback_transaction_with_null_originalTransactionId_fail(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(1D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).txnType(Transaction.TxnType.ROLLBACK).build())
                .expectStatus().isBadRequest();
    }


    @Test
    void test_fetch_transaction_history_success(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(1D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();

        fetchTransactionHistoryExchange(webClient,1L, null,null,null, null,null)
                .expectStatus().is2xxSuccessful();
    }

    @Test
    void test_fetch_transaction_history_two_pages_success(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(10D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();


        fetchTransactionHistoryExchange(webClient,1L, null,  null,null,0, 1)
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(2);
    }

    @Test
    void test_fetch_transaction_history_txnType_filter_success(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(10D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();


        fetchTransactionHistoryExchange(webClient,1L, Transaction.TxnType.CREDIT,  null,null,0, 1)
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(0);
    }

    @Test
    void test_fetch_transaction_history_from_date_invalid_fail(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(10D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();


        fetchTransactionHistoryExchange(webClient,1L, Transaction.TxnType.CREDIT, null,LocalDate.now().toString() ,0, 1)
                .expectStatus().isBadRequest();
    }

    @Test
    void test_fetch_transaction_history_to_date_invalid_fail(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(10D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();


        fetchTransactionHistoryExchange(webClient,1L, Transaction.TxnType.CREDIT, LocalDate.now().toString(), null ,0, 1)
                .expectStatus().isBadRequest();
    }


    @Test
    void test_fetch_transaction_history_valid_date_range_success(@Autowired WebTestClient webClient) {

        insertUserAccount(UserAccount.builder()
                .firstName("john").lastName("smith").balance(10D).currency("EUR").build());


        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();

        performTransactionExchange(webClient, 1l, TransactionRequest.builder().accountId(1L).amount(1D).txnType(Transaction.TxnType.DEBIT).build())
                .expectStatus().is2xxSuccessful();


        fetchTransactionHistoryExchange(webClient,1L, Transaction.TxnType.DEBIT,LocalDate.now().toString(), LocalDate.now().toString() ,0, 10)
                .expectBody()
                .jsonPath("$.totalElements").isEqualTo(2)
                .jsonPath("$.totalPages").isEqualTo(1);
    }

    /*
    {
  "content" : [ ],
  "pageable" : {
    "pageNumber" : 0,
    "pageSize" : 1,
    "sort" : {
      "empty" : false,
      "unsorted" : false,
      "sorted" : true
    },
    "offset" : 0,
    "paged" : true,
    "unpaged" : false
  },
  "last" : true,
  "totalElements" : 0,
  "totalPages" : 0,
  "size" : 1,
  "sort" : {
    "empty" : false,
    "unsorted" : false,
    "sorted" : true
  },
  "number" : 0,
  "first" : true,
  "numberOfElements" : 0,
  "empty" : true
}
     */

    private static WebTestClient.@NotNull ResponseSpec fetchTransactionHistoryExchange(WebTestClient webClient,
                                                                                       Long id,
                                                                                       Transaction.TxnType type,
                                                                                       String from, String to,
                                                                                       Integer page, Integer size) {
        StringBuilder uri =  new StringBuilder("/accounts/"+id+"/transactions?");

        if(type!=null){
            uri.append("txnType=").append(type);
        }
        if(from!=null){
            uri.append("&from=").append(from);
        }
        if(to!=null){
            uri.append("&to=").append(to);
        }
        if(page!=null){
            uri.append("&page=").append(page);
        }
        if(size!=null){
            uri.append("&size=").append(size);
        }

        return webClient
                .get().uri(uri.toString())
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }


    private static WebTestClient.@NotNull ResponseSpec performTransactionExchange(WebTestClient webClient, Long id, TransactionRequest transactionRequest) {
        return webClient
                .post().uri("/accounts/"+id+"/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(transactionRequest)
                .exchange();
    }


    private void insertUserAccount(UserAccount userAccount) {
       this.userAccountRepository.save(userAccount).as(StepVerifier::create).expectNextCount(1).verifyComplete();
    }
}
