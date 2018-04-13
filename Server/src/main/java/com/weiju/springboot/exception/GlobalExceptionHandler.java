package com.weiju.springboot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = BaseException.class)
    @ResponseBody
    public ResponseEntity<ErrorInfo> jsonErrorHamdler(HttpServletRequest req, BaseException e) throws Exception {
        ErrorInfo r = new ErrorInfo();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        r.setDetail(exceptionAsString);
        r.setStatus(e.getStatus());
        r.setTitle(e.getMessage());
        ResponseEntity responseEntity = new ResponseEntity(r, e.getStatus());
        return responseEntity;
    }
}
