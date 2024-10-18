package org.demo.useraccounts.repository;

import org.demo.useraccounts.model.UserAccount;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserAccountRepository extends ReactiveCrudRepository<UserAccount, Long> {

    @Query("SELECT * FROM user_accounts WHERE last_name = :lastname")
    Flux<UserAccount> findByLastName(String lastName);


    @Modifying
    @Query("UPDATE user_accounts SET is_deleted = true WHERE id = :id")
    Mono<Void> softDeleteById(Long id);
}