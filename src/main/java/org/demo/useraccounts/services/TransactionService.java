package org.demo.useraccounts.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.BaseRuntimeException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.repository.TransactionRepository;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Service class for handling transactions related operations.
 */
@Getter
@Service
@Slf4j
public class TransactionService {


    private final UserAccountRepository userAccountRepository;
    private final TransactionRepository transactionRepository;

    TransactionService(UserAccountRepository userAccountRepository, TransactionRepository transactionRepository) {
        this.userAccountRepository = userAccountRepository;
        this.transactionRepository = transactionRepository;
    }


    /**
     * Handles a transaction based on its type (DEBIT, CREDIT, ROLLBACK).
     *
     * @param transaction The transaction to be processed.
     * @return A Mono of Transaction representing the processed transaction.
     */
    @Transactional
    public Mono<Transaction> handleTransaction(Transaction transaction) {
        return switch (transaction.getTxnType()) {
            case DEBIT -> handleDebit(transaction);
            case CREDIT -> handleCredit(transaction);
            case ROLLBACK -> handleRollback(transaction);
        };
    }

    /**
     * Handles a ROLLBACK transaction.
     *
     * @param transaction The ROLLBACK transaction to be processed.
     * @return A Mono of Transaction representing the processed ROLLBACK transaction.
     */
    private Mono<Transaction> handleRollback(Transaction transaction) {
        return transactionRepository.findById(Objects.requireNonNull(transaction.getOriginalTransactionId()))
                .flatMap(originalTransaction -> {
                    if (originalTransaction.getTxnType() != Transaction.TxnType.CREDIT && originalTransaction.getTxnType() != Transaction.TxnType.DEBIT)
                        Mono.error(new BaseException("Cannot rollback transaction %s. Should be reconciled manually.".formatted(originalTransaction.getId())
                                , ErrorCode.INVALID_REQUEST));

                    Transaction.TxnType txnType = originalTransaction.getTxnType() == Transaction.TxnType.CREDIT
                            ? Transaction.TxnType.DEBIT
                            : Transaction.TxnType.CREDIT;

                    transaction.setOriginalTransactionType(originalTransaction.getTxnType());
                    transaction.setStatus(Transaction.TransactionStatus.COMPLETED);

                    return handleTransaction(Transaction.builder().amount(originalTransaction.getAmount())
                            .txnType(txnType)
                            .status(Transaction.TransactionStatus.COMPLETED)
                            .originalTransactionId(originalTransaction.getId()).currency(originalTransaction.getCurrency()).userAccountId(originalTransaction.getUserAccountId()).build())
                            .then(transactionRepository.save(transaction));

                });
    }

    /**
     * Handles a CREDIT transaction.
     *
     * @param transaction The CREDIT transaction to be processed.
     * @return A Mono of Transaction representing the processed CREDIT transaction.
     */
    private Mono<Transaction> handleCredit(Transaction transaction) {
        return userAccountRepository.creditAmount(transaction.getUserAccountId(), transaction.getAmount())
                .filter(count -> count > 0)
                .flatMap(count -> {
                    return userAccountRepository.findById(transaction.getUserAccountId())
                            .flatMap(updatedAccount -> {
                                transaction.setBalance(updatedAccount.getBalance());
                                return transactionRepository.save(transaction);
                            });
                });
    }

    /**
     * Handles a DEBIT transaction.
     *
     * @param transaction The DEBIT transaction to be processed.
     * @return A Mono of Transaction representing the processed DEBIT transaction.
     */
    private Mono<Transaction> handleDebit(Transaction transaction) {
        return userAccountRepository.debitAmount(transaction.getUserAccountId(), transaction.getAmount())
                .handle((updateCount, sink) -> {
                    if (updateCount == 0)
                        sink.error(new BaseRuntimeException("Insufficient balance!", ErrorCode.INVALID_REQUEST));
                    else
                        sink.next(updateCount);
                })
                .flatMap(updateCount -> {
                    return userAccountRepository.findById(transaction.getUserAccountId())
                            .flatMap(updatedAccount -> {
                                transaction.setBalance(updatedAccount.getBalance());
                                return transactionRepository.save(transaction);
                            });
                });
    }

    /**
     * Aggregates the total amount for transactions based on user account ID, transaction type, and status.
     *
     * @param userAccountID The ID of the user account.
     * @param txnType       The type of transaction (CREDIT, DEBIT, ROLLBACK).
     * @param status        The status of the transaction (IN_PROGRESS, COMPLETED, FAILED, REVERTED).
     * @return A Mono of Double representing the aggregated amount.
     */
    public Mono<Double> aggregateByUserAccountIdAndTxnTypeAndStatus(Long userAccountID, Transaction.TxnType txnType, Transaction.TransactionStatus status) {
        return transactionRepository.aggregateByUserAccountIdAndTxnTypeAndStatusAndDate(userAccountID, txnType, status, LocalDate.now());
    }
}
