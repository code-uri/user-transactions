package org.demo.useraccounts.exceptions;

import org.springframework.web.server.ResponseStatusException;

public interface SystemError  {

    public ErrorCode getErrorCode();


    public String getMessage();
}
