package org.demo.useraccounts.controllers;

import lombok.Getter;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.model.BaseEntity;
import org.demo.useraccounts.repository.BaseRepository;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Getter
public abstract class AbstractCRUDController <T extends BaseEntity<ID>, ID> {

    private final BaseRepository<T, ID> repository;

    protected AbstractCRUDController(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<T> findById(@PathVariable ID id) {
       return repository.findById(id)
               .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)));
    }

    @PostMapping
    public Mono<T> create(@RequestBody T entity) {
        return repository.findById(entity.getId())
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)));
    }

    @PutMapping("/{id}")
    public Mono<T>update(@PathVariable ID id, @RequestBody T entity) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                .flatMap(t -> {
                   return  repository.save(entity);
                });
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable ID id) {
       return repository.existsById(id)
               .switchIfEmpty(Mono.error(new BaseException(ErrorCode.RESOURCE_NOT_FOUND)))
                .flatMap(exists -> repository.deleteById(id));
    }
}
