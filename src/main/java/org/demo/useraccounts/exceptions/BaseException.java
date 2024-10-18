package org.demo.useraccounts.exceptions;

import lombok.Getter;
import org.demo.useraccounts.errorcodes.ErrorCode;

@Getter
public class BaseException extends Exception{

    ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, Throwable cause){
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
