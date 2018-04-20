package com.weiju.springboot.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends Exception {
    //HTTP status code
    private HttpStatus status;

    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BaseException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
