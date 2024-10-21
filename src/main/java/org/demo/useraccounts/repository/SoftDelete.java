//package org.demo.useraccounts.repository;
//
//import org.reactivestreams.Publisher;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
//import org.springframework.data.r2dbc.repository.Modifying;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.NoRepositoryBean;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.io.Serializable;
//
//@NoRepositoryBean
//public interface SoftDelete<T, ID extends Serializable>  {
//
//
//    @Query("UPDATE #{#tableName} SET is_deleted = true WHERE id = :id")
//    @Modifying
//    Mono<Void> softDeleteById(ID id);
//}
