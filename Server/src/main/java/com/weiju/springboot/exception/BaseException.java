package com.weiju.springboot.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends Exception {
    //HTTP status code
    private HttpStatus status;
    private String detail;
    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public BaseException(String message, HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public BaseException(String message, String detail,HttpStatus status) {
        super(message);
        this.status = status;
        this.detail = detail;
    }

    public BaseException(String message ,String detail,HttpStatus status, Throwable cause) {
        super(message, cause);
        this.status = status;
        this.detail = detail;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public void setStatus(HttpStatus status){
        this.status = status;
    }

    public void setDetail(String detail){
        this.detail = detail;
    }
}
