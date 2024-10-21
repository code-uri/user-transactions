/*
package org.demo.useraccounts.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.demo.useraccounts.services.UserAccountService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Getter
@RestController
@RequestMapping(value = "/accounts")
@Slf4j
public class UserAccountsController extends AbstractCRUDController<UserAccount, Long> implements UserAccountService{

    UserAccountRepository repository;

    protected UserAccountsController(UserAccountRepository repository) {
        super(repository);
        this.repository = repository;
    }


    @Override
    public Mono<Void> deleteById(@PathVariable Long id) {
        return getRepository().suspendAccount(id);
    }

}*/
