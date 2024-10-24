package org.demo.useraccounts;

import jakarta.annotation.Nullable;
import org.demo.useraccounts.dto.DateRange;
import org.demo.useraccounts.dto.TransactionRequest;
import org.demo.useraccounts.exceptions.BaseRuntimeException;
import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.TransactionRepository;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.demo.useraccounts.services.TransactionService;
import org.demo.useraccounts.services.UserTransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@Testcontainers
@SpringBootTest(classes = InfrastructureConfiguration.class)
@TestPropertySource( properties = {
        "spring.r2dbc.url=r2dbc:tc:mysql:///databasename?TC_IMAGE_TAG=8.0.36",
        "spring.r2dbc.username=user",
        "spring.r2dbc.password=password",
        "spring.sql.init.mode=ALWAYS",
        "logging.level.org.springframework.r2dbc.core=DEBUG",
        "logging.level.reactor.netty.http.client=DEBUG",
        "junit.jupiter.execution.parallel.enabled=false"
})
public class UserTransactionServiceTests {

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

    @SpyBean
    TransactionService transactionService;

    @Autowired
    UserTransactionService userTransactionService;


    @Test
    void test_transaction_unknown_account_expect_error() {

        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(10D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .as(StepVerifier::create)
                .expectErrorMatches(throwable -> {
                    return throwable instanceof BaseRuntimeException e
                            && e.getErrorCode().getHttpStatus() == HttpStatus.NOT_FOUND;
                })
                .verify();
    }

    @Test
    void test_transaction_account_1_insufficient_balance_error() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(0D).currency("EUR").build());

        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(10D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .as(StepVerifier::create)
                .expectErrorMatches(throwable -> {
                    return throwable instanceof BaseRuntimeException e
                            && e.getErrorCode().getHttpStatus() == HttpStatus.BAD_REQUEST;
                })
                .verify();
    }

    @Test
    void test_transaction_account_1_success() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(10D).currency("EUR").build());

        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(5D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .as(StepVerifier::create)
                .assertNext(transactionResponse -> {
                    Assertions.assertNotNull( transactionResponse.getTransactionRefId());
                    Assertions.assertEquals(1, transactionResponse.getAccountId());
                    Assertions.assertEquals(Transaction.TransactionStatus.COMPLETED, transactionResponse.getStatus());
                    Assertions.assertNotNull(transactionResponse.getTimestamp());
                })
                .verifyComplete();

        this.userAccountRepository.findById(1L).as(StepVerifier::create)
                .assertNext(userAccount -> Assertions.assertEquals(5, userAccount.getBalance()));
    }


    @Test
    void test_rollback_success() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(10D).currency("EUR").build());


        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(5D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .flatMap(transactionResponse -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                          .txnType(Transaction.TxnType.ROLLBACK)
                          .originalTransactionId(transactionResponse.getTransactionRefId())
                          .remarks("Rollback").build()))
                .as(StepVerifier::create)
                .assertNext(transactionResponse -> {
                    Assertions.assertNotNull( transactionResponse.getTransactionRefId());
                })
                .verifyComplete();

        this.userAccountRepository.findById(1L).as(StepVerifier::create)
                .assertNext(userAccount -> Assertions.assertEquals(10, userAccount.getBalance()));
    }



    @Test
    void test_rollback_a_rollback_transaction_fail() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(10D).currency("EUR").build());


        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(5D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .flatMap(transactionResponse -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .txnType(Transaction.TxnType.ROLLBACK)
                        .originalTransactionId(transactionResponse.getTransactionRefId())
                        .remarks("Rollback").build())
                )
                .flatMap(transactionResponse -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .txnType(Transaction.TxnType.ROLLBACK)
                        .originalTransactionId(transactionResponse.getTransactionRefId())
                        .remarks("Rollback").build()))
                .as(StepVerifier::create)
                .expectError();

        this.userAccountRepository.findById(1L).as(StepVerifier::create)
                .assertNext(userAccount -> Assertions.assertEquals(10, userAccount.getBalance()));
    }


    @Test
    void test_transaction_limit_error() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("10100")).currency("EUR").build());

        AtomicLong transactionId = new AtomicLong();
        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(Double.parseDouble("10000")).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .as(StepVerifier::create)
                .assertNext(transactionResponse -> {
                    Assertions.assertNotNull( transactionResponse.getTransactionRefId());
                    Assertions.assertEquals(1, transactionResponse.getAccountId());
                    Assertions.assertEquals(Transaction.TransactionStatus.COMPLETED, transactionResponse.getStatus());
                    Assertions.assertNotNull(transactionResponse.getTimestamp());

                    transactionId.set(transactionResponse.getTransactionRefId());
                })
                .verifyComplete();

        this.userAccountRepository.findById(1L).as(StepVerifier::create)
                .assertNext(userAccount -> Assertions.assertEquals(100, userAccount.getBalance())).verifyComplete();


        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(Double.parseDouble("100")).txnType(Transaction.TxnType.DEBIT)
                        .remarks("Test").build())
                .as(StepVerifier::create)
                .expectErrorMatches(throwable -> {
                    return throwable instanceof BaseRuntimeException e
                            && e.getErrorCode().getHttpStatus() == HttpStatus.PRECONDITION_FAILED;
                })
                .verify();

    }


    @Test
    void test_exceed_transaction_limit_next_day_success() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("10100")).currency("EUR").build());

        when(transactionService.aggregateByUserAccountIdAndTxnTypeAndStatus(anyLong(), any(), any())).thenCallRealMethod();

        AtomicLong transactionId = new AtomicLong();
        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(Double.parseDouble("10000")).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .as(StepVerifier::create)
                .assertNext(transactionResponse -> {
                    Assertions.assertNotNull( transactionResponse.getTransactionRefId());
                    Assertions.assertEquals(1, transactionResponse.getAccountId());
                    Assertions.assertEquals(Transaction.TransactionStatus.COMPLETED, transactionResponse.getStatus());
                    Assertions.assertNotNull(transactionResponse.getTimestamp());

                    transactionId.set(transactionResponse.getTransactionRefId());
                })
                .verifyComplete();

        this.userAccountRepository.findById(1L).as(StepVerifier::create)
                .assertNext(userAccount -> Assertions.assertEquals(100, userAccount.getBalance())).verifyComplete();



        when(transactionService.aggregateByUserAccountIdAndTxnTypeAndStatus(anyLong(), any(), any())).thenReturn(Mono.just(100D));

        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(Double.parseDouble("100D")).txnType(Transaction.TxnType.DEBIT).remarks("Test").build())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        this.userAccountRepository.findById(1L).as(StepVerifier::create)
                .assertNext(userAccount -> Assertions.assertEquals(0, userAccount.getBalance())).verifyComplete();


    }


    @Test
    void test_all_transaction_history_success() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("100")).currency("EUR").build());

        Flux.range(0, 100)
                .flatMap(aLong -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(Double.parseDouble("1D")).txnType(Transaction.TxnType.DEBIT).remarks("Test").build()))
                .as(StepVerifier::create)
                .expectNextCount(100)
                .verifyComplete();

        this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                .amount(Double.parseDouble("100D")).txnType(Transaction.TxnType.CREDIT).remarks("Test").build())
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();



        this.userTransactionService.transactionHistory(1L, null,null, PageRequest.of(0, 10000))
            .as(StepVerifier::create)
                .assertNext(page -> {
                    Assertions.assertEquals(101, page.getTotalElements());
                }).verifyComplete();


        this.userTransactionService.transactionHistory(1L, null,null, PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .assertNext(page -> {
                    Assertions.assertEquals(11, page.getTotalPages());
                }).verifyComplete();

        this.userTransactionService.transactionHistory(1L, null, Transaction.TxnType.DEBIT, PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .assertNext(page -> {
                    Assertions.assertEquals(100, page.getTotalElements());
                }).verifyComplete();

        this.userTransactionService.transactionHistory(1L, null, Transaction.TxnType.CREDIT, PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .assertNext(page -> {
                    Assertions.assertEquals(1, page.getTotalElements());
                }).verifyComplete();


        this.userTransactionService.transactionHistory(1L,
                        DateRange.builder().from(LocalDate.now().minusDays(1))
                                .to(LocalDate.now().minusDays(1)).build(),
                        Transaction.TxnType.CREDIT, PageRequest.of(0, 10))
                .as(StepVerifier::create)
                .assertNext(page -> {
                    Assertions.assertEquals(0, page.getTotalElements());
                }).verifyComplete();

    }



    @Test
    void test_all_transaction_history_zero_amt_debit_fail() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("100")).currency("EUR").build());

        Flux.range(0, 100)
                .flatMap(aLong -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(0D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build()))
                .as(StepVerifier::create)
                .expectError()
                .verify();

    }

    @Test
    void test_all_transaction_history_negative_amt_debit_fail() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("100")).currency("EUR").build());


        Flux.range(0, 100)
                .flatMap(aLong -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(-1D).txnType(Transaction.TxnType.DEBIT).remarks("Test").build()))
                .as(StepVerifier::create)
                .expectError()
                .verify();

    }

    @Test
    void test_all_transaction_history_zero_amt_credit_fail() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("100")).currency("EUR").build());


        Flux.range(0, 100)
                .flatMap(aLong -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(0D).txnType(Transaction.TxnType.CREDIT).remarks("Test").build()))
                .as(StepVerifier::create)
                .expectError()
                .verify();

    }

    @Test
    void test_all_transaction_history_negative_amt_credit_fail() {
        insertUserAccount(UserAccount.builder()
                .firstName("John").lastName("Smith").balance(Double.parseDouble("100")).currency("EUR").build());

        Flux.range(0, 100)
                .flatMap(aLong -> this.userTransactionService.handleTransaction(1L, TransactionRequest.builder()
                        .amount(-1D).txnType(Transaction.TxnType.CREDIT).remarks("Test").build()))
                .as(StepVerifier::create)
                .expectError()
                .verify();

    }

    private void insertUserAccount(UserAccount userAccount) {
        this.userAccountRepository.save(userAccount).as(StepVerifier::create).expectNextCount(1).verifyComplete();
    }
}
