package org.demo.useraccounts.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;


@SuperBuilder
@Table("transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction extends BaseEntity<Long> {

    private Long userAccountId;
    private TxnType type;
    private double amount;
    private String currency;

    public static enum TxnType {
        CREDIT, DEBIT;
    }
}