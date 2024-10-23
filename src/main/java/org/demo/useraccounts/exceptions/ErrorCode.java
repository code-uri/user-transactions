package org.demo.useraccounts.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Represents error codes for different types of errors in the application.
 */
@Getter
public enum ErrorCode {

    INTERNAL_ERROR("internal.error", "Internal error", HttpStatus.INTERNAL_SERVER_ERROR),
    RESOURCE_NOT_FOUND("resource.not.found", "Resource not found", HttpStatus.NOT_FOUND),
    INVALID_REQUEST("invalid.request", "Invalid request", HttpStatus.BAD_REQUEST),
    CONDITION_FAILED("condition.failed", "Condition failed", HttpStatus.PRECONDITION_FAILED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Constructor for ErrorCode enum.
     *
     * @param code       The error code.
     * @param message    The error message.
     * @param httpStatus The HTTP status associated with the error.
     */
    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}