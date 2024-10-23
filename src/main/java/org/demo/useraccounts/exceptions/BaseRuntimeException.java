package org.demo.useraccounts.exceptions;

import lombok.Getter;
import org.springframework.web.server.ResponseStatusException;

/**
 * BaseRuntimeException class represents a custom exception that can be thrown with an ErrorCode.
 */
@Getter
public class BaseRuntimeException extends ResponseStatusException implements SystemError {

    ErrorCode errorCode;

    /**
     * Constructs a new BaseRuntimeException with the specified ErrorCode.
     *
     * @param errorCode The ErrorCode associated with the exception.
     */
    public BaseRuntimeException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BaseRuntimeException with the specified message and ErrorCode.
     *
     * @param message The detail message.
     * @param errorCode The ErrorCode associated with the exception.
     */
    public BaseRuntimeException(String message, ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BaseRuntimeException with the specified ErrorCode and cause.
     *
     * @param errorCode The ErrorCode associated with the exception.
     * @param cause The cause of the exception.
     */
    public BaseRuntimeException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getHttpStatus(), errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}