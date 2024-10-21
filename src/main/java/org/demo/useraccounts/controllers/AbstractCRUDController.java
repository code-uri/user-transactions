package org.demo.useraccounts.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.BaseEntity;
import org.demo.useraccounts.model.UserAccount;
import org.demo.useraccounts.repository.BaseRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * Abstract CRUD Controller for handling CRUD operations on entities.
 */
@Slf4j
@Getter
public abstract class AbstractCRUDController <T extends BaseEntity<ID>, ID extends Serializable> {

    private final ReactiveCrudRepository<T, ID> repository;

    /**
     * Constructor for AbstractCRUDController.
     *
     * @param repository The repository for the entity type T.
     */
    protected AbstractCRUDController(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    /**
     * Retrieve an entity by ID.
     *
     * @param id The ID of the entity to retrieve.
     * @return A Mono emitting the entity if found, otherwise emits an error.
     */
    @GetMapping("/{id}")
    public Mono<T> findById(@PathVariable ID id) {
       return repository.findById(id)
               .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)));
    }

    /**
     * Create a new entity.
     *
     * @param entity The entity to create.
     * @return A Mono emitting the created entity.
     */
    @Operation(summary = "Create a new entity", description = "Creates a new entity in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public Mono<T> create(@RequestBody T entity) {
        return repository.save(entity);
    }

    /**
     * Update an existing entity by ID.
     *
     * @param id The ID of the entity to update.
     * @param entity The updated entity.
     * @return A Mono emitting the updated entity if found, otherwise emits an error.
     */
    @PutMapping("/{id}")
    public Mono<T>update(@PathVariable ID id, @RequestBody T entity) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                .flatMap(t -> {
                   return  repository.save(entity);
                });
    }

    /**
     * Delete an entity by ID.
     *
     * @param id The ID of the entity to delete.
     * @return A Mono emitting void if the entity is deleted successfully, otherwise emits an error.
     */
    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable ID id) {
       return repository.deleteById(id);
    }
}
