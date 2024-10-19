package org.demo.useraccounts.advices;

import org.demo.useraccounts.exceptions.BaseException;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;

@ControllerAdvice
class WebResponseEntityExceptionHandler extends org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler {

    @ExceptionHandler
    @ResponseBody
    ProblemDetail handleBadCredentials(BaseException e, ServerWebExchange exchange) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(e.getErrorCode().getHttpStatus(), e.getMessage());
        problemDetail.setTitle(e.getErrorCode().getMessage());
        //problemDetail.setInstance();
        problemDetail.setType(exchange.getRequest().getURI());
        return problemDetail;
    }
}