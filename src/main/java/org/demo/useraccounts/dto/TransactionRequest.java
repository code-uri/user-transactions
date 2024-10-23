package org.demo.useraccounts.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.demo.useraccounts.model.Transaction;

@Builder
@Getter
@Setter
@ToString
public class TransactionRequest {

    Long accountId;
    @Nonnull
    Transaction.TxnType txnType;
    @Nullable
    Long originalTransactionId;
    @Nullable
    Double amount;
    @Nullable
    String currency;
    @Nullable
    String remarks;

}
