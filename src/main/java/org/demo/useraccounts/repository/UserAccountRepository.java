package org.demo.useraccounts.repository;

import org.demo.useraccounts.model.UserAccount;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

public interface UserAccountRepository  extends DeletedNotAllowed<UserAccount, Long>, BaseRepository<UserAccount, Long> {

    @Query("UPDATE user_accounts SET status = 'SUSPENDED' WHERE id = :id")
    @Modifying
    Mono<Void> suspendAccountById(Long id);
}