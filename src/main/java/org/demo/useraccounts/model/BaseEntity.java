package org.demo.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@SuperBuilder
@Data
@NoArgsConstructor
public class BaseEntity<ID> {

    @Id
    private ID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime createdOn;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime modifiedOn;

}
