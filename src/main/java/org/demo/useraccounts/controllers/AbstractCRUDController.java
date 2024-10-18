package org.demo.useraccounts.controllers;

import org.demo.useraccounts.errorcodes.ErrorCode;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.model.BaseEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public abstract class AbstractCRUDController <T extends BaseEntity<ID>, ID> {

    private final ReactiveCrudRepository<T, ID> repository;

    protected AbstractCRUDController(ReactiveCrudRepository<T, ID> repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public Flux<T> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<T> findById(@PathVariable ID id) {
       return repository.findById(id)
               .switchIfEmpty(Mono.error(new BaseException(ErrorCode.ENTITY_NOT_FOUND)));
    }

    @PostMapping
    public Mono<T> create(@RequestBody T entity) {
        return repository.findById(entity.getId())
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.ENTITY_NOT_FOUND)));
    }

    @PutMapping("/{id}")
    public Mono<T>update(@PathVariable ID id, @RequestBody T entity) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.ENTITY_NOT_FOUND)))
                .flatMap(t -> {
                   return  repository.save(entity);
                });
    }

    @PatchMapping("/{id}")
    public Mono<T> partialUpdate(@PathVariable ID id, @RequestBody T updates) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.ENTITY_NOT_FOUND)))
                .flatMap(entity -> {
                    return repository.save(updates);
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable ID id) {
       return repository.existsById(id)
               .switchIfEmpty(Mono.error(new BaseException(ErrorCode.ENTITY_NOT_FOUND)))
                .flatMap(exists -> repository.deleteById(id));
    }
}
