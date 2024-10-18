package org.demo.useraccounts.errorcodes;

public enum ErrorCode {

    ENTITY_NOT_FOUND("entity.not.found", "Entity not found");

    final String code;
    final String message;

    ErrorCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public static ErrorCode getInstance() {
        return ENTITY_NOT_FOUND;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
