package org.demo.useraccounts.model;

import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;


@SuperBuilder
@Data
@Table("transactions")
public class Transaction extends BaseEntity<Long> {

    private final Long userAccountId;
    private final TxnType type;
    private final double amount;
    private final String currency;

    public static enum TxnType {
        CREDIT, DEBIT;
    }
}