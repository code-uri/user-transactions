package org.demo.useraccounts.routes;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.dto.DateRange;
import org.demo.useraccounts.dto.TransactionRequest;
import org.demo.useraccounts.dto.TransactionResponse;
import org.demo.useraccounts.exceptions.BaseRuntimeException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.demo.useraccounts.services.UserTransactionService;
import org.demo.useraccounts.validators.DefaultSpringBeanValidator;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.content.Builder.contentBuilder;
import static org.springdoc.core.fn.builders.exampleobject.Builder.exampleOjectBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder;
import static org.springdoc.core.fn.builders.schema.Builder.schemaBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Slf4j
@Configuration
public class UserTransactionRoutesConfig {
    public static final String INVALID_USER_ACCOUNT_ID_SUPPLIED = "Invalid User Account ID supplied";
    public static final String RESOURCE_NOT_FOUND = "Error: Not Found";
    public static final String OK = "OK";
    public static final String USER_ACCOUNT_ID = "User Account Id";
    private static final String EXAMPLE_DEBIT_REQUEST = """
            {
              "txnType": "DEBIT",
              "amount": 10,
              "remarks": "Oooo.."
            }
            """;

    private static final String EXAMPLE_CREDIT_REQUEST = """
            {
              "txnType": "CREDIT",
              "amount": 10,
              "remarks": "Oooo.."
            }
            """;

    private static final String EXAMPLE_ROLLBACK_REQUEST = """
            {
              "originalTransactionId": 1,
              "txnType": "ROLLBACK",
              "remarks": "rollback transaction id 1"
            }
            """;
    private static final String EXAMPLE_TRANSACTION_RESPONSE = """
            {
              "accountId": 1,
              "transactionRefId": 123,
              "status": "COMPLETED",
              "timestamp": "2024-10-22T16:30:44.849Z"
            }
            """;


    private final UserTransactionService userTransactionService;
    private final DefaultSpringBeanValidator defaultSpringBeanValidator;

    public UserTransactionRoutesConfig(UserTransactionService service, DefaultSpringBeanValidator defaultSpringBeanValidator) {
        this.userTransactionService = service;
        this.defaultSpringBeanValidator = defaultSpringBeanValidator;
    }


    @Bean
    RouterFunction<ServerResponse> userTransactionRoutes() {
        return route().POST("/accounts/{id}/transactions",
                        handleTransaction(), handleTransactionOpenAPI()).build()
                .and(route().GET("/accounts/{id}/transactions", transactionHistory(), transactionHistoryOpenAPI()).build());

    }


    public HandlerFunction<ServerResponse> handleTransaction() {

        return req ->  req.bodyToMono(TransactionRequest.class).flatMap(transactionRequest
                        -> userTransactionService.handleTransaction(idSupplier(req).get(),
                        defaultSpringBeanValidator.validate(transactionRequest)))
                .flatMap(o -> ok().bodyValue(o));
    }

    private Consumer<Builder> handleTransactionOpenAPI() {
        return ops -> ops.tag("transactions")
                .operationId("submitTransaction").summary("Submit transaction request.").tags(new String[]{"MyTransactions"})
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("User Account Id").required(true))
                .requestBody(requestBodyBuilder()
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(TransactionRequest.class))
                                .example(exampleOjectBuilder().summary("Credit request").value(EXAMPLE_CREDIT_REQUEST).name("credit request"))
                                .example(exampleOjectBuilder().summary("Debit request").value(EXAMPLE_DEBIT_REQUEST).name("debit request"))
                                .example(exampleOjectBuilder().summary("Rollback request").value(EXAMPLE_ROLLBACK_REQUEST).name("rollback request"))
                        ))
                .response(responseBuilder().responseCode("200").description(OK)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(UserAccount.class))
                                .example(exampleOjectBuilder().value(EXAMPLE_TRANSACTION_RESPONSE))
                        ))
                .response(responseBuilder().responseCode("404").description(RESOURCE_NOT_FOUND));
    }


    public HandlerFunction<ServerResponse> transactionHistory() {
        return req ->
                ok().contentType(MediaType.APPLICATION_JSON)
                        .body(Mono.defer(() -> {
                                    return userTransactionService.transactionHistory(idSupplier(req).get(),
                                            getDateRange(req),
                                            getTxnType(req),
                                            getPageable(req));
                                })
                                , Transaction.class);

    }


    private Consumer<Builder> transactionHistoryOpenAPI() {
        return ops -> ops.tag("transactions")
                .operationId("transactionHistory").summary("Get transaction history").tags(new String[]{"MyTransactions"})
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("User Account Id").required(true))
                .parameter(parameterBuilder().in(ParameterIn.QUERY).name("from").description("From date").example("2024-10-01"))
                .parameter(parameterBuilder().in(ParameterIn.QUERY).name("to").description("To date").example("2034-10-01"))
                .parameter(parameterBuilder().in(ParameterIn.QUERY).name("txnType").description("Transaction Type").implementation(Transaction.TxnType.class))
                .parameter(parameterBuilder().in(ParameterIn.QUERY).name("page").description("page").example("0"))
                .parameter(parameterBuilder().in(ParameterIn.QUERY).name("size").description("size").example("10"))
                .response(responseBuilder().responseCode("200").description(OK)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder()
                                .implementation(Page.class))))
                .response(responseBuilder().responseCode("404").description(RESOURCE_NOT_FOUND));
    }


    private static PageRequest getPageable(ServerRequest req) {
        try {
            return PageRequest.of(Integer.parseInt(req.queryParam("page").orElse("0")),
                    Integer.parseInt(req.queryParam("size").orElse("10")),
                    Sort.by(Sort.Direction.ASC, "id"));
        } catch (Exception e) {
            throw new BaseRuntimeException(e.getMessage(), ErrorCode.INVALID_REQUEST);
        }
    }

    private static Transaction.TxnType getTxnType(ServerRequest req) {
        return req.queryParam("txnType").map(s -> {
            try {
                return Transaction.TxnType.valueOf(s);
            } catch (Exception e) {
                throw new BaseRuntimeException("Invalid txnType %s".formatted(s), ErrorCode.INVALID_REQUEST);
            }
        }).orElse(null);
    }

    private static DateRange getDateRange(ServerRequest req) {
        Optional<String> from= req.queryParam("from");
        Optional<String> to= req.queryParam("to");
        if(from.isPresent() && to.isEmpty()){
            throw new BaseRuntimeException("To date is required", ErrorCode.INVALID_REQUEST);
        }
        else if(to.isPresent() && from.isEmpty()){
            throw new BaseRuntimeException("From date is required", ErrorCode.INVALID_REQUEST);
        }
        else if(from.isPresent() && to.isPresent()){
            try {
                return DateRange.builder()
                        .from(req.queryParam("from").map(LocalDate::parse).orElse(null))
                        .to(req.queryParam("to").map(LocalDate::parse).orElse(null))
                        .build();
            } catch (Exception e) {
                throw new BaseRuntimeException(e.getMessage(), ErrorCode.INVALID_REQUEST);
            }
        }
        return null;
    }

    private static Supplier<Long> idSupplier(ServerRequest req) {
        return () -> {
            try {
                return Long.valueOf(req.pathVariable("id"));
            } catch (NumberFormatException e) {
                throw new BaseRuntimeException("id %s is not a number".formatted(req.pathVariable("id")), ErrorCode.INVALID_REQUEST);
            }
        };
    }


}
