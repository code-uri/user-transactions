package org.demo.useraccounts.services;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.demo.useraccounts.model.UserAccount;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface UserAccountService  {


    @PostMapping
    Mono<UserAccount> create(@RequestBody(description = "Create User Account",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserAccount.class),
                    examples = @ExampleObject(value = """
                            {
                             "firstName": "John",
                             "lastName": "Doe",
                             "balance": 1000.50
                            }
                            """))) UserAccount entity);

    /**
     * Retrieve an UserAccount by ID.
     *
     * @param id The ID of the UserAccount to retrieve.
     * @return A Mono emitting the UserAccount if found, otherwise emits an error.
     */
    @GetMapping("/{id}")
     Mono<UserAccount> findById(@PathVariable Long id);

    /**
     * Update an UserAccount by ID.
     *
     * @param id     The ID of the UserAccount to update.
     * @param entity The updated UserAccount.
     * @return A Mono emitting the updated UserAccount if found, otherwise emits an error.
     */
    @PutMapping("/{id}")
     Mono<UserAccount> update(@PathVariable Long id, @RequestBody(description = "Update User Account",
            required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserAccount.class),
                    examples = @ExampleObject(value = """
                            {
                             "id": 1
                             "firstName: "John",
                             "lastName": "Doe",
                             "balance": 1000.50
                            }
                            """))) UserAccount entity);


    /**
     * SUSPEND UserAccount by ID.
     *
     * @param id The ID of UserAccount
     * @return A Mono emitting void if the UserAccount is SUSPENDED successfully, otherwise emits an error.
     */
    @DeleteMapping("/{id}")
    Mono<Void> deleteById(@PathVariable Long id);

}
