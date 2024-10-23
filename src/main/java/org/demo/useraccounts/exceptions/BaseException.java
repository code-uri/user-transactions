package org.demo.useraccounts.exceptions;

import lombok.Getter;

/**
 * BaseException class represents a custom exception that can be thrown with an ErrorCode.
 */
@Getter
public class BaseException extends Exception  implements SystemError {

    ErrorCode errorCode;

    /**
     * Constructs a new BaseException with the specified ErrorCode.
     *
     * @param errorCode The ErrorCode associated with the exception.
     */
    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BaseException with the specified message and ErrorCode.
     *
     * @param message The detail message.
     * @param errorCode The ErrorCode associated with the exception.
     */
    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Constructs a new BaseException with the specified ErrorCode and cause.
     *
     * @param errorCode The ErrorCode associated with the exception.
     * @param cause The cause of the exception.
     */
    public BaseException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}