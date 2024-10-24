package org.demo.useraccounts.services;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.demo.useraccounts.dto.DateRange;
import org.demo.useraccounts.dto.TransactionRequest;
import org.demo.useraccounts.dto.TransactionResponse;
import org.demo.useraccounts.exceptions.BaseException;
import org.demo.useraccounts.exceptions.BaseRuntimeException;
import org.demo.useraccounts.exceptions.ErrorCode;
import org.demo.useraccounts.model.Transaction;
import org.demo.useraccounts.repository.TransactionRepository;
import org.demo.useraccounts.repository.UserAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class for handling user transactions.
 * This class is intended to contain business logic related to user transactions.
 */
@Slf4j
@Getter
@Service
public class UserTransactionService {

    public static final int THRESHOLD_AMOUNT = 10_000;
    private final UserAccountRepository userAccountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    /**
     * Constructor for UserTransactionService class.
     *
     * @param transactionRepository The repository for transactions.
     * @param userAccountRepository The repository for user accounts.
     * @param transactionService The service for handling transactions.
     * @param r2dbcEntityTemplate entityTemplate for custom queries.
     */
    public UserTransactionService(TransactionRepository transactionRepository,
                                  UserAccountRepository userAccountRepository,
                                  TransactionService transactionService,
                                  R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.transactionRepository = transactionRepository;
        this.userAccountRepository = userAccountRepository;
        this.transactionService = transactionService;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * Handles a transaction request for a specific user account.
     *
     * @param accountId The ID of the user account.
     * @param request The transaction request details.
     * @return a Mono of TransactionResponse.
     */
    @Transactional
    public Mono<TransactionResponse> handleTransaction(@Nonnull Long accountId, @Nonnull TransactionRequest request) {
        log.info("received id {} request {}",accountId, request);

        return getUserAccountRepository().findById(accountId)
                .doOnNext(userAccount -> {
                    log.info("found useraccount {}", userAccount);
                })
                .switchIfEmpty(Mono.error(new BaseRuntimeException(ErrorCode.RESOURCE_NOT_FOUND)))
                .flatMap(userAccount -> {
                    request.setAccountId(userAccount.getId());
                    request.setCurrency(userAccount.getCurrency());
                    return validate(request);
                })

                .flatMap(transactionService::handleTransaction)
                .map(transaction -> {
                    return TransactionResponse.builder()
                            .accountId(transaction.getUserAccountId())
                            .status(transaction.getStatus())
                            .timestamp(transaction.getCreatedOn())
                            .transactionRefId(transaction.getId())
                            .build();
                })
                .switchIfEmpty(Mono.error(new BaseException(ErrorCode.INTERNAL_ERROR)));
    }

    /**
     * Validates the transaction request based on the transaction type.
     *
     * @param request The transaction request to validate.
     * @return a Mono of Transaction.
     */
    private Mono<Transaction> validate(TransactionRequest request) {
        return switch (request.getTxnType()) {
            case DEBIT, CREDIT -> validateDebitTransaction(request);
            case ROLLBACK -> validateRollbackTransaction(request);
        };
    }


    /**
     * Creates a new Transaction object based on the TransactionRequest.
     *
     * @param request The transaction request to create a transaction from.
     * @return a Transaction object.
     */
    private Transaction createTransaction(TransactionRequest request) {
        return Transaction.builder()
                .amount(request.getAmount())
                .txnType(request.getTxnType())
                .status(Transaction.TransactionStatus.COMPLETED)
                .currency(request.getCurrency())
                .originalTransactionId(request.getOriginalTransactionId())
                .userAccountId(request.getAccountId())
                .build();
    }

    /**
     * Validates a debit transaction request.
     *
     * @param request The debit transaction request to validate.
     * @return a Mono of Transaction.
     */
    private Mono<Transaction> validateDebitTransaction(TransactionRequest request) {
        Objects.requireNonNull(request.getAmount());
        if(request.getAmount() < 1)
            return Mono.error(new BaseRuntimeException("Transaction amount should be greater-than 0", ErrorCode.INVALID_REQUEST));

        return transactionService.aggregateByUserAccountIdAndTxnTypeAndStatus(
                        request.getAccountId(),
                        Transaction.TxnType.DEBIT,
                        Transaction.TransactionStatus.COMPLETED)
                .switchIfEmpty(Mono.just(0D))
                .handle((amount, sink) -> {
                    if (amount + request.getAmount() > THRESHOLD_AMOUNT) {
                        sink.error(new BaseRuntimeException("Transaction limit %d exceeded."
                                .formatted(THRESHOLD_AMOUNT), ErrorCode.CONDITION_FAILED));
                    } else {
                        sink.next(createTransaction(request));
                    }
                });
    }

    /**
     * Validates a rollback transaction request.
     *
     * @param request The rollback transaction request to validate.
     * @return a Mono of Transaction.
     */
    private Mono<Transaction> validateRollbackTransaction(TransactionRequest request) {

        if (request.getOriginalTransactionId()==null) {
            return Mono.error(new BaseRuntimeException("Original Transaction ID cannot be null for ROLLBACK request", ErrorCode.INVALID_REQUEST));
        }

        return transactionRepository.findByOriginalTransactionIdAndTxnTypeAndTransactionStatus(
                        request.getOriginalTransactionId(),
                        Transaction.TxnType.ROLLBACK,
                        Transaction.TransactionStatus.COMPLETED)
                .doOnNext(transaction -> {
                    throw new BaseRuntimeException("Transaction with id %s has already been reverted on %s."
                            .formatted(request.getOriginalTransactionId(), transaction.getCreatedOn()),
                            ErrorCode.INVALID_REQUEST);
                })
                .thenReturn(createTransaction(request));
    }

    /**
     * Retrieves the transaction history for a user account based on filters.
     *
     * @param userAccount The ID of the user account.
     * @param dateRange The date range filter.
     * @param txnType The transaction type filter.
     * @param pageable The pagination information.
     * @return a Mono of Page<Transaction>.
     */
    public Mono<Page<Transaction>> transactionHistory(Long userAccount, @Nullable DateRange dateRange, @Nullable Transaction.TxnType txnType, Pageable pageable) {

        List<Criteria> criteriaList = new ArrayList<>();
        criteriaList.add(Criteria.where("user_account_id").is(userAccount));

        if (txnType != null) {
            criteriaList.add(Criteria.where("txn_type").is(txnType));
        }

        if (dateRange != null) {

            criteriaList.add(Criteria.where("created_on").greaterThanOrEquals(dateRange.getFrom().atStartOfDay()));
            criteriaList.add(Criteria.where("created_on").lessThanOrEquals(dateRange.getTo().plusDays(1).atStartOfDay()));
        }

        return transactionRepository.getPage(r2dbcEntityTemplate, Criteria.from(criteriaList), pageable, Transaction.class);
    }
}