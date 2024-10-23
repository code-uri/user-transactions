package org.demo.useraccounts.repository;

import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.model.UserAccount;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Repository interface for UserAccount entity operations.
 * Extends DeletedNotAllowed and BaseRepository interfaces for additional functionality.
 */
public interface UserAccountRepository extends DeletedNotAllowed<UserAccount, Long>, BaseRepository<UserAccount, Long> {

    /**
     * Suspends a user account by setting its status to 'SUSPENDED'.
     *
     * @param id the id of the user account to suspend
     * @return a Mono<Void> signaling the completion of the suspension operation
     */
    @Query("UPDATE user_accounts SET status = 'SUSPENDED' WHERE id = :id")
    @Modifying
    Mono<Void> suspendAccountById(Long id);

    @Modifying
    @Transactional
    @Query("UPDATE user_accounts u SET u.balance = u.balance - :amount WHERE u.id = :id AND u.balance >= :amount")
    Mono<Long> debitAmount(Long id, Double amount);

    @Modifying
    @Transactional
    @Query("UPDATE user_accounts u SET u.balance = u.balance + :amount WHERE u.id = :id")
    Mono<Long> creditAmount(Long id, Double amount);
}