package org.demo.useraccounts.exceptions;

/**
 * Represents a system error interface that provides methods to retrieve error code and message.
 */
public interface SystemError {

    /**
     * Retrieves the error code associated with the system error.
     *
     * @return The ErrorCode enum representing the error code.
     */
    public ErrorCode getErrorCode();

    /**
     * Retrieves the error message associated with the system error.
     *
     * @return A String containing the error message.
     */
    public String getMessage();
}