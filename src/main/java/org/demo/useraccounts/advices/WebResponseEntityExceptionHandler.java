package org.demo.useraccounts.advices;

import org.demo.useraccounts.exceptions.BaseException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.server.ServerWebExchange;

import java.sql.SQLException;

@ControllerAdvice
class WebResponseEntityExceptionHandler extends org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler {

    // Specify name of a specific view that will be used to display the error:
    @ExceptionHandler({SQLException.class, DataAccessException.class})
    public RedirectView databaseError(Exception e) {
        e.printStackTrace();
        RedirectView view = new RedirectView("/errors/error.html");
        view.setStatusCode(HttpStatus.FOUND);
        return view;
    }

    // Total control - setup a model and return the view name yourself. Or
    // consider subclassing ExceptionHandlerExceptionResolver (see below).
    @ExceptionHandler(BaseException.class)
    @ResponseBody
    public ProblemDetail handleError(ServerWebExchange exchange, BaseException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getErrorCode().getHttpStatus(), e.getMessage());
        problemDetail.setTitle(e.getErrorCode().getMessage());
        //problemDetail.setInstance();
        problemDetail.setType(exchange.getRequest().getURI());
        return problemDetail;
    }
}