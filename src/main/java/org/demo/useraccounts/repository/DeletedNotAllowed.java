/*
package org.demo.useraccounts.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

*/
/**
 * This interface extends ReactiveCrudRepository to provide basic CRUD functionality with restrictions on delete operations.
 * Operations related to delete are not allowed and will throw UnsupportedOperationException when invoked.
 *
 * @param <T>  the type of the entity
 * @param <ID> the type of the entity's identifier
 *//*

@NoRepositoryBean
public interface DeletedNotAllowed<T, ID> extends ReactiveCrudRepository<T, ID> {

    */
/**
     * Throws UnsupportedOperationException as delete by ID operation is not allowed.
     *
     * @param id the ID of the entity to delete
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> deleteById(ID id){
        throw new UnsupportedOperationException();
    }

    */
/**
     * Throws UnsupportedOperationException as delete by ID operation is not allowed.
     *
     * @param id the ID of the entity to delete
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> deleteById(Publisher<ID> id){
        throw new UnsupportedOperationException();
    }

    */
/**
     * Throws UnsupportedOperationException as delete operation is not allowed.
     *
     * @param entity the entity to delete
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> delete(T entity){
        throw new UnsupportedOperationException();
    }

    */
/**
     * Throws UnsupportedOperationException as delete all by IDs operation is not allowed.
     *
     * @param ids the IDs of the entities to delete
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> deleteAllById(Iterable<? extends ID> ids){
        throw new UnsupportedOperationException();
    }

    */
/**
     * Throws UnsupportedOperationException as delete all entities operation is not allowed.
     *
     * @param entities the entities to delete
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> deleteAll(Iterable<? extends T> entities){
        throw new UnsupportedOperationException();
    }

    */
/**
     * Throws UnsupportedOperationException as delete all entities operation is not allowed.
     *
     * @param entityStream a stream of entities to delete
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> deleteAll(Publisher<? extends T> entityStream){
        throw new UnsupportedOperationException();
    }

    */
/**
     * Throws UnsupportedOperationException as delete all entities operation is not allowed.
     *
     * @return a Mono that immediately signals an error with UnsupportedOperationException
     *//*

    @Override
    default Mono<Void> deleteAll(){
        throw new UnsupportedOperationException();
    }
}*/
