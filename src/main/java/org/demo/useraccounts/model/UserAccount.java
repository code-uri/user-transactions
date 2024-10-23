package org.demo.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;


@SuperBuilder
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Table("user_accounts")
public class UserAccount extends BaseEntity<Long> {

    private String firstName;
    private String lastName;
    private Double balance;
    private String currency = "EUR";

    @JsonIgnore()
    private UserAccountStatus status;

    public static enum UserAccountStatus {
        ACTIVE,
        SUSPENDED
    }
}