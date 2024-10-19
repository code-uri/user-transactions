package org.demo.useraccounts.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

@NoRepositoryBean
public interface BaseRepository <T, ID> extends ReactiveCrudRepository<T, ID> , SoftDelete<T,ID> {


}
