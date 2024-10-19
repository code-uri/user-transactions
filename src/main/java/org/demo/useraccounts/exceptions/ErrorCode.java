package org.demo.useraccounts.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    RESOURCE_NOT_FOUND("resource.not.found", "Resource not found", HttpStatus.NOT_FOUND);

    final String code;
    final String message;
    final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus){
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public static ErrorCode getInstance() {
        return RESOURCE_NOT_FOUND;
    }

}
