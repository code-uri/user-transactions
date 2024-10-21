package org.demo.useraccounts.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@NoRepositoryBean
public interface BaseRepository <T, ID> extends ReactiveCrudRepository<T, ID>  {

}
