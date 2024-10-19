package org.demo.useraccounts.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

public interface SoftDelete <T, ID> {

    @Modifying
    @Query("UPDATE #{tableName} SET is_deleted = true WHERE id = ?1")
    Mono<Void> deleteById(ID id);

    @Modifying
    @Query("UPDATE #{tableName} SET is_deleted = true WHERE id = ?#{entity.id}")
    Mono<Void> delete(T entity);

    @Modifying
    @Query("UPDATE #{tableName} SET is_deleted = true")
    Mono<Void> deleteAll();
}
