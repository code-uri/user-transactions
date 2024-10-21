package org.demo.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;


@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@Table("user_accounts")
public class UserAccount extends BaseEntity<Long> {

    private String firstName;
    private String lastName;
    private double balance;

    @JsonIgnore()
    private Status status;

    public static enum Status{
        ACTIVE,
        SUSPENDED
    }
}