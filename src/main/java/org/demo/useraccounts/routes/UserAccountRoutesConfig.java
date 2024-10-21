package org.demo.useraccounts.routes;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.Employee;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.springdoc.core.fn.builders.operation.Builder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

import static org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder;
import static org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder;
import static org.springdoc.webflux.core.fn.SpringdocRouteBuilder.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;


@Configuration
public class UserAccountRoutesConfig {

    private final UserAccountRepository service;

    public UserAccountRoutesConfig(UserAccountRepository service) {
        this.service = service;
    }

    @Bean
    RouterFunction<ServerResponse> userAccountRoutes() {
        return route().GET("/accounts/{id}",
                req -> ok().body(
                        service.findById(Long.valueOf(req.pathVariable("id"))), UserAccount.class), findEmployeeByIdOpenAPI()  ).build()

                .and(route().POST("/accounts", updateEmployeeFunction(), saveUserAccountOpenAPI()).build())

                .and(route().PUT("/accounts/{id}",
                        update(), updateUserAccountOpenAPI()).build())

                .and(route().DELETE("/accounts/{id}",
                        req -> ok().body(
                                service.suspendAccountById(Long.valueOf(req.pathVariable("id"))), UserAccount.class),
                        deleteByIdOpenAPI()).build());

    }

    HandlerFunction<ServerResponse> delete(){
        return req -> ok().body(
                service.suspendAccountById(Long.valueOf(req.pathVariable("id"))), UserAccount.class);
    }

    public HandlerFunction<ServerResponse> update(){
        return req -> req.body(BodyExtractors.toMono(UserAccount.class))
                .flatMap(account -> service.findById(Long.valueOf(req.pathVariable("id")))
                        .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                        .flatMap(service::save))	.then(ok().build());
    }

    private HandlerFunction<ServerResponse> updateEmployeeFunction() {
        return req -> req.body(BodyExtractors.toMono(UserAccount.class))
                .doOnNext(service::save)
                .then(ok().build());
    }


    private Consumer<Builder> findEmployeeByIdOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("findById").summary("Find by ID").tags(new String[] { "UserAccount" })
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("UserAccount Id"))
                .response(responseBuilder().responseCode("200").description("successful operation").implementation(UserAccount.class))
                .response(responseBuilder().responseCode("400").description("Invalid User Account ID supplied"))
                .response(responseBuilder().responseCode("404").description("Resource not found"));
    }

    private Consumer<Builder> deleteByIdOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("deleteById").summary("Delete by ID").tags(new String[] { "UserAccount" })
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("User Account Id"))
                .response(responseBuilder().responseCode("400").description("Invalid User Account ID supplied"))
                .response(responseBuilder().responseCode("404").description("Resource not found"));
    }


    private Consumer<Builder> updateUserAccountOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("save").summary("Update user account").tags(new String[] { "UserAccount" })
                .parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("User Account Id"))
                .response(responseBuilder().responseCode("200").description("successful operation").implementation(UserAccount.class))
                .response(responseBuilder().responseCode("400").description("Invalid Employee ID supplied"))
                .response(responseBuilder().responseCode("404").description("Employee not found"));
    }


    private Consumer<Builder> saveUserAccountOpenAPI() {
        return ops -> ops.tag("accounts")
                .operationId("save").summary("Create user account").tags(new String[] { "UserAccount" })
                //.parameter(parameterBuilder().in(ParameterIn.PATH).name("id").description("Employee Id"))
                .response(responseBuilder().responseCode("200").description("successful operation").implementation(UserAccount.class))
                .response(responseBuilder().responseCode("404").description("Resource not found"));
    }
}
