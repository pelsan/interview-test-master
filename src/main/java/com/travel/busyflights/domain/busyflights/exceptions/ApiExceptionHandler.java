package com.travel.busyflights.domain.busyflights.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BussinesRuleException.class)
    public ResponseEntity<ExceptionResponse> handleBussinesRuleException(BussinesRuleException ex) {
        ExceptionResponse response = new ExceptionResponse("Validation error",ex.getCode(),ex.getMessage());
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

}
