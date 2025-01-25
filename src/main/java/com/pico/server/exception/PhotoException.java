package com.pico.server.exception;

public class PhotoException extends BaseException{
    public PhotoException(ErrorCode errorCode) {
        super(errorCode);
    }
}
