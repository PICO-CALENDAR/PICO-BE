package com.pico.server.exception;

public class UserException extends BaseException {

    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
