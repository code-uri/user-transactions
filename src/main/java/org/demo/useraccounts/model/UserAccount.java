package org.demo.useraccounts.model;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@SuperBuilder
@Data
@Table("user_accounts")
//@Document("orders-#{tenantService.getOrderCollection()}-${tenant-config.suffix}")
public class UserAccount extends BaseEntity<Long> {

    private final String firstName;
    private final String lastName;
    private double balance;

}