package org.demo.useraccounts.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

@NoRepositoryBean
public interface DeletedNotAllowed<T, ID> extends ReactiveCrudRepository<T, ID> {


    @Override
   default Mono<Void> deleteById(ID id){
        throw new UnsupportedOperationException();
    }

    @Override
    default Mono<Void> deleteById(Publisher<ID> id){
        throw new UnsupportedOperationException();
    }

    @Override
    default Mono<Void> delete(T entity){
        throw new UnsupportedOperationException();
    }

    @Override
    default Mono<Void> deleteAllById(Iterable<? extends ID> ids){
        throw new UnsupportedOperationException();
    }

    @Override
    default Mono<Void> deleteAll(Iterable<? extends T> entities){
        throw new UnsupportedOperationException();
    }

    @Override
    default Mono<Void> deleteAll(Publisher<? extends T> entityStream){
        throw new UnsupportedOperationException();
    }

    @Override
    default Mono<Void> deleteAll(){
        throw new UnsupportedOperationException();
    }
}
