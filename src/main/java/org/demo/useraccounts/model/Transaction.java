package org.demo.useraccounts.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@SuperBuilder
@Table("transactions")
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class Transaction extends BaseEntity<Long> {

    @Nonnull
    private Long userAccountId;
    @Nonnull
    private TxnType txnType;

    private Double amount;

    private Double balance;
    @Nonnull
    private String currency;
    @Nullable
    Long originalTransactionId;
    @Nullable
    TxnType originalTransactionType;
    @Nonnull
    private TransactionStatus status;

    public static enum TxnType {
        CREDIT, DEBIT, ROLLBACK
    }


    public static enum TransactionStatus{
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        REVERTED;
    }
}