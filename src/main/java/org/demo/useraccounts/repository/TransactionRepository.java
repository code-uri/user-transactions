package org.demo.useraccounts.repository;

import org.demo.useraccounts.dto.DateRange;
import org.demo.useraccounts.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.demo.useraccounts.model.Transaction.TxnType;
/**
 * Interface for handling transactions in the repository.
 * Extends DeletedNotAllowed and BaseRepository interfaces.
 */
public interface TransactionRepository extends DeletedNotAllowed<Transaction, Long>, BaseRepository<Transaction, Long> {

    /**
     * Find transactions by user account ID.
     *
     * @param userAccountId the ID of the user account
     * @return a Flux of transactions
     */
    @Query("SELECT * FROM transactions WHERE user_account_id = :userAccountId")
    Flux<Transaction> findByUserAccountID(Long userAccountId);

    /**
     * Find transactions by user account ID and transaction type.
     *
     * @param userAccountId the ID of the user account
     * @param txnType       the type of transaction (CREDIT or DEBIT)
     * @return a Flux of transactions
     */
    @Query("SELECT * FROM transactions WHERE user_account_id = :userAccountId and txn_type = :txnType")
    Flux<Transaction> findByUserAccountIDAndTxnType(Long userAccountId, TxnType txnType);

    @Query("SELECT SUM(amount) FROM transactions  WHERE user_account_id = :userAccountId AND txn_type = :txnType and status = :status AND DATE(created_on) = :currentDate" )
    Mono<Double> aggregateByUserAccountIdAndTxnTypeAndStatusAndDate(Long userAccountId, TxnType txnType, Transaction.TransactionStatus status, LocalDate currentDate);


    @Query("SELECT * FROM transactions WHERE original_transaction_id = :originalTransactionId and txn_type = :txnType and status = :status ORDER BY id DESC LIMIT 1")
    Mono<Transaction> findByOriginalTransactionIdAndTxnTypeAndTransactionStatus(Long originalTransactionId, Transaction.TxnType txnType, Transaction.TransactionStatus status);

}