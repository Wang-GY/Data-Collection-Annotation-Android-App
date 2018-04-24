package com.weiju.springboot.exception;

import org.json.JSONObject;
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
    public ResponseEntity<String> jsonErrorHamdler(HttpServletRequest req, BaseException e) throws Exception {
        ErrorInfo r = new ErrorInfo();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        r.setDetail(exceptionAsString);
        r.setStatus(e.getStatus());
        r.setTitle(e.getMessage());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("error", r);
        ResponseEntity responseEntity = new ResponseEntity(jsonObject.toString(), e.getStatus());
        e.printStackTrace();
        return responseEntity;
    }
}
