package org.demo.useraccounts.exceptions;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;

import java.sql.SQLException;
/**
 * WebResponseEntityExceptionHandler class handles exceptions globally for the application.
 */
@ControllerAdvice
class WebResponseEntityExceptionHandler extends org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler {

    /**
     * Handles database-related errors and redirects to a specific error view.
     *
     * @param e The exception to be handled.
     * @return A RedirectView object pointing to the error view.
     */
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public ProblemDetail databaseError(ServerWebExchange exchange, Exception e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }

    /**
     * Handles custom BaseException errors and returns a ProblemDetail object with error details.
     *
     * @param exchange The ServerWebExchange object.
     * @param e The BaseException to be handled.
     * @return A ProblemDetail object with error details.
     */
    @ExceptionHandler(value = { BaseException.class})
    @ResponseBody
    public ProblemDetail handleError(ServerWebExchange exchange, SystemError e) {
        return handleSystemError(exchange, e);
    }

    @ExceptionHandler(value = { BaseRuntimeException.class})
    @ResponseBody
    public ProblemDetail handleBaseRuntimeError(ServerWebExchange exchange, SystemError e) {
        return handleSystemError(exchange, e);
    }

    private static ProblemDetail handleSystemError(ServerWebExchange exchange, SystemError e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getErrorCode().getHttpStatus(), e.getMessage());
        problemDetail.setTitle(e.getErrorCode().getMessage());
        //problemDetail.setInstance();
        problemDetail.setType(exchange.getRequest().getURI());
        return problemDetail;
    }
}