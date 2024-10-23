package org.demo.useraccounts.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Represents a base entity with common fields like id, createdOn, and modifiedOn.
 */
@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor
public class BaseEntity<ID> {

    /**
     * Unique identifier for the entity.
     */
    @Id
    private ID id;

    /**
     * Date and time when the entity was created.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime createdOn;

    /**
     * Date and time when the entity was last modified.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    LocalDateTime modifiedOn;

}