package org.demo.useraccounts.routes;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.BaseRuntimeException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.demo.useraccounts.validators.DefaultSpringBeanValidator;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

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


@Configuration
@Slf4j
public class UserAccountRoutesConfig {

    private final UserAccountRepository service;
    private final DefaultSpringBeanValidator defaultSpringBeanValidator;

    public UserAccountRoutesConfig(UserAccountRepository service, DefaultSpringBeanValidator defaultSpringBeanValidator) {
        this.service = service;
        this.defaultSpringBeanValidator = defaultSpringBeanValidator;
    }

    @Bean
    RouterFunction<ServerResponse> userAccountRoutes() {
        return route().GET(ACCOUNTS + "/{id}", findById(), findByIdOpenAPI()).build()
                .and(route().POST(ACCOUNTS, saveUserAccount(), saveUserAccountOpenAPI()).build())
                .and(route().PUT(ACCOUNTS + "/{id}", updateUserAccount(), updateUserAccountOpenAPI()).build())
                .and(route().DELETE(ACCOUNTS + "/{id}", suspendAccountById(), suspendAccountByIdOpenAPI()).build());

    }

    private HandlerFunction<ServerResponse> findById() {
        return req -> ok().body(
                service.findById(idSupplier(req).get()).switchIfEmpty(Mono.error(new BaseRuntimeException(ErrorCode.RESOURCE_NOT_FOUND))),
                UserAccount.class);
    }

    private Consumer<Builder> findByIdOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("findById").summary("Find by ID").tags(new String[]{"UserAccount"})
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("UserAccount Id"))
                .response(responseBuilder().responseCode("200").description(OK)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(UserAccount.class))
                                .example(exampleOjectBuilder().value(EXAMPLE_USER_ACCOUNT_RESPONSE))
                        ))
                .response(responseBuilder().responseCode("404").description(RESOURCE_NOT_FOUND));
    }

    private HandlerFunction<ServerResponse> saveUserAccount() {
        return req -> defaultSpringBeanValidator.validateAndGet(UserAccount.class, req, service::save)
                .flatMap(o -> ok().bodyValue(o));
    }

    private Consumer<Builder> saveUserAccountOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("save").summary("Create user account").tags(new String[]{"UserAccount"})
                //.parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("Employee Id"))
                .requestBody(requestBodyBuilder()
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(UserAccount.class))
                                .example(exampleOjectBuilder().summary("Create a user account").value(EXAMPLE_USER_ACCOUNT)
                                )))
                .response(responseBuilder().responseCode("200").description(OK)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(UserAccount.class))
                                .example(exampleOjectBuilder().value(EXAMPLE_USER_ACCOUNT_RESPONSE))
                        ))
                .response(responseBuilder().responseCode("404").description("Resource not found"));
    }

    public HandlerFunction<ServerResponse> updateUserAccount() {
        return req -> req.body(BodyExtractors.toMono(UserAccount.class))
                .flatMap(account -> service.findById(idSupplier(req).get()).thenReturn(account)
                        .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                        .flatMap(userAccount -> {

                            return defaultSpringBeanValidator.validateAndGet(UserAccount.class, req,
                                    service::save)
                                    .doOnNext(next -> {
                                        log.info("next {}", next);
                                    });
                        }))
                .flatMap(userAccount
                        -> ok().bodyValue(userAccount));
    }

    private Consumer<Builder> updateUserAccountOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("save").summary("Update user account").tags(new String[]{"UserAccount"})
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description(USER_ACCOUNT_ID))
                .requestBody(requestBodyBuilder()
                        .content(contentBuilder()
                                .mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(UserAccount.class))
                                .example(exampleOjectBuilder().summary("Update user account").value(EXAMPLE_USER_ACCOUNT)
                                )))
                .response(responseBuilder().responseCode("200").description(OK)
                        .content(contentBuilder().mediaType(MediaType.APPLICATION_JSON.toString()).schema(schemaBuilder().implementation(UserAccount.class))
                                .example(exampleOjectBuilder().value(EXAMPLE_USER_ACCOUNT_RESPONSE))
                        ))
                .response(responseBuilder().responseCode("404").description(RESOURCE_NOT_FOUND));
    }


    HandlerFunction<ServerResponse> suspendAccountById() {
        return req -> {
            Long id = idSupplier(req).get();
            return req.body(BodyExtractors.toMono(UserAccount.class))
                    .flatMap(account -> service.existsById(id)
                            .filter(exists -> !exists)
                            .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                            .flatMap(exists -> service.suspendAccountById(id))).then(ok().build());
        };
    }

    private Consumer<Builder> suspendAccountByIdOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("suspendAccountById").summary("Suspend user account").tags(new String[]{"UserAccount"})
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description(USER_ACCOUNT_ID))
                .response(responseBuilder().responseCode("200").description(OK))
                .response(responseBuilder().responseCode("404").description(RESOURCE_NOT_FOUND));
    }


    private static Supplier<Long> idSupplier(ServerRequest req) {
        return () -> {
            try {
                return Long.valueOf(req.pathVariable("id"));
            } catch (NumberFormatException e) {
                throw new BaseRuntimeException("id %s is not a number".formatted(req.pathVariable("id")), ErrorCode.INVALID_REQUEST);
            } catch (IllegalArgumentException e) {
                throw new BaseRuntimeException(e.getMessage(), ErrorCode.INVALID_REQUEST);
            }
        };
    }


    public static final String RESOURCE_NOT_FOUND = "Error: Not Found";
    public static final String OK = "OK";
    public static final String USER_ACCOUNT_ID = "User Account Id";
    private static final String ACCOUNTS = "/accounts";
    private static final String EXAMPLE_USER_ACCOUNT_RESPONSE = """
            {
                "id": 1,
              "firstName": "Raghu",
              "lastName": "Koduri",
              "balance": 0,
              "currency": "EUR"
            }
            """;
    private static final String EXAMPLE_USER_ACCOUNT = """
            {
              "firstName": "Raghu",
              "lastName": "Koduri",
              "balance": 0,
              "currency": "EUR"
            }
            """;
}
