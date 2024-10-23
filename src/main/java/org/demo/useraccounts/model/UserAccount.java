package org.demo.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.*;
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

    @NotNull
    @Size(min = 3, max = 25)
    private String firstName;

    @NotNull
    @Size(min = 3, max = 25)
    private String lastName;
    @NotNull
    @PositiveOrZero
    private Double balance;

    @NotNull
    @Size(min = 3, max = 25)
    private String currency = "EUR";

    @NotNull
    private UserAccountStatus status = UserAccountStatus.ACTIVE;

    public static enum UserAccountStatus {
        ACTIVE,
        SUSPENDED
    }
}