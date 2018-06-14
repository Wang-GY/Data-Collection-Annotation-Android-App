package com.weiju.springboot.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ResponseEntity<Object> jsonErrorHamdler(HttpServletRequest req, BaseException e) throws Exception {
        ErrorInfo err = new ErrorInfo();
        err.setDetail(e.getDetail());
        err.setStatus(e.getStatus());
        err.setTitle(e.getMessage());
        Map<String,Object> errors = new LinkedHashMap<>();
        errors.put("errors",err);
        ResponseEntity responseEntity = new ResponseEntity(errors, e.getStatus());
        e.printStackTrace();
        return responseEntity;
    }
}
