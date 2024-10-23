package org.demo.useraccounts.dto;

import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.demo.useraccounts.model.Transaction;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
public class TransactionResponse {

    @Nonnull
    Long accountId;
    @Nonnull
    Long transactionRefId;
    @Nonnull
    Transaction.TransactionStatus status;
    @Nonnull
    LocalDateTime timestamp;
}
