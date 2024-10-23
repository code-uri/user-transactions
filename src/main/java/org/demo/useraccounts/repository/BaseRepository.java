package org.demo.useraccounts.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.data.relational.core.query.Query.query;

/**
 * Base repository interface for reactive CRUD operations.
 * This interface extends ReactiveCrudRepository to provide basic CRUD functionality.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 */
@NoRepositoryBean
public interface BaseRepository<T, ID> extends ReactiveCrudRepository<T, ID> {


    /**
     * Utility function which retrieves a page of entities based on the provided criteria and pageable information.
     *
     * @param template the R2dbcEntityTemplate used for database operations
     * @param criteria the criteria to filter the entities
     * @param pageable the paging information
     * @param clz the class type of the entity
     * @return a Mono emitting a Page containing the entities and total count
     */
    default Mono<Page<T>> getPage(R2dbcEntityTemplate template, Criteria criteria, Pageable pageable, Class<T> clz) {
        Mono<List<T>> list = template.select(clz)
                .matching(query(criteria).with(pageable))
                .all()
                .collectList();
        Mono<Long> count = template.select(clz)
                .matching(query(criteria))
                .count();
        return Mono.zip(list, count)
                .map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }
}