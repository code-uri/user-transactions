package org.demo.useraccounts.repository;

import org.demo.useraccounts.model.Transaction;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

import static org.demo.useraccounts.model.Transaction.TxnType;

public interface TransactionRepository extends DeletedNotAllowed<Transaction, Long>, BaseRepository<Transaction, Long> {

    @Query("SELECT * FROM transactions WHERE user_account_id = :userAccountId")
    Flux<Transaction> findByUserAccountID(Long userAccountId);

    @Query("SELECT * FROM transactions WHERE user_account_id = :userAccountId and txn_type = :txnType")
    Flux<Transaction> findByUserAccountIDAndTxnType(Long userAccountId, TxnType txnType);

}