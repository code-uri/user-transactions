package org.demo.useraccounts.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * This interface defines a contract for soft deletion of entities.
 * It provides a method to soft delete an entity by setting a flag.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
@NoRepositoryBean
public interface SoftDelete<T, ID extends Serializable>  {

    /**
     * Soft deletes an entity by updating the 'is_deleted' flag to true.
     *
     * @param id the identifier of the entity to be soft deleted
     * @return a Mono that completes once the entity is soft deleted
     */
    @Query("UPDATE #{#tableName} SET is_deleted = true WHERE id = :id")
    @Modifying
    Mono<Void> softDeleteById(ID id);
}