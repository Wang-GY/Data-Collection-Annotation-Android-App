package com.weiju.springboot.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorInfo {


    private int status;
    private Object title;

    public int getStatus() {
        return status;
    }

    public Object getTitle() {
        return title;
    }

    public Object getDetail() {
        return detail;
    }

    private Object detail;


    public void setStatus(HttpStatus status) {
        this.status = status.value();
    }

    public void setTitle(Object title) {
        this.title = title;
    }

    public void setDetail(Object detail) {
        this.detail = detail;
    }


}
