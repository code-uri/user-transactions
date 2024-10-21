package org.demo.useraccounts.routes;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
public class UserAccountRoutesConfig {

    private final UserAccountRepository service;

    public UserAccountRoutesConfig(UserAccountRepository service) {
        this.service = service;
    }

    @Bean
    @RouterOperations(
            {
                    @RouterOperation(path = "/accounts/{id}", operation = @Operation(operationId = "findById", summary = "Find User Account by ID",
                            tags = {"Accounts"},
                            method = "GET",
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Account Id")},
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = UserAccount.class))),
                                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal error")})),


                    @RouterOperation(path = "/accounts",  operation = @Operation(operationId = "create", summary = "Create user account",
                            tags = {"Accounts"},
                            method = "POST",
                            requestBody = @RequestBody(description = "Create user account",
                                    required = true,
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = UserAccount.class),
                                            examples = @ExampleObject(value = """
                                                    {
                                                     "firstName": "John",
                                                     "lastName": "Doe",
                                                     "balance": 1000.50
                                                    }
                                                    """))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = UserAccount.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "500", description = "Internal error")})),

                    @RouterOperation(path = "/accounts/{id}", operation = @Operation(operationId = "update", summary = "Update user account",
                            tags = {"Accounts"},
                            method = "PUT",
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Account Id")},
                            requestBody = @RequestBody(description = "Update user account",
                                    required = true,
                                    content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = UserAccount.class),
                                            examples = @ExampleObject(value = """
                                                    {
                                                     "firstName": "John",
                                                     "lastName": "Doe",
                                                     "balance": 1000.50
                                                    }
                                                    """))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "OK",
                                            content = @Content(schema = @Schema(implementation = UserAccount.class))),
                                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                                    @ApiResponse(responseCode = "400", description = "Invalid request"),
                                    @ApiResponse(responseCode = "500", description = "Internal error")})),

                    @RouterOperation(path = "/accounts/{id}", operation = @Operation(operationId = "suspendAccountById", summary = "Suspend User Account by ID",
                            tags = {"Accounts"},
                            method = "DELETE",
                            parameters = {@Parameter(in = ParameterIn.PATH, name = "id", description = "User Account Id")},
                            responses = {@ApiResponse(responseCode = "200", description = "OK",
                                    content = @Content(schema = @Schema(implementation = UserAccount.class))),
                                    @ApiResponse(responseCode = "404", description = "Resource not found"),
                                    @ApiResponse(responseCode = "500", description = "Internal error")}))
            })
    RouterFunction<ServerResponse> userAccountRoutes() {
        return route(GET("/accounts/{id}"),
                req -> ok().body(
                        service.findById(Long.valueOf(req.pathVariable("id"))), UserAccount.class))
                .and(route(POST("/accounts"),
                        req -> ok().body(req.bodyToMono(UserAccount.class).flatMap(service::save), UserAccount.class)))
                .and(route(PUT("/accounts/{id}"),
                        req -> ok().body(req.bodyToMono(UserAccount.class)
                                .flatMap(account -> service.findById(Long.valueOf(req.pathVariable("id")))
                                        .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                                .flatMap(service::save)), UserAccount.class)))
                .and(route(DELETE("/accounts/{id}"),
                        req -> ok().body(
                                service.suspendAccountById(Long.valueOf(req.pathVariable("id"))), UserAccount.class)));

    }
}
