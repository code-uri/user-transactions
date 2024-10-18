package org.demo.useraccounts.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Getter
@SuperBuilder
@Data
public class BaseEntity<ID> {

    @Id
    private ID id;

    boolean is_deleted;

    Date createdOn;

    Date modifiedOn;

}
