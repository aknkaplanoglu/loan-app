package com.akotako.loanservice.aop;

import com.akotako.loanservice.model.exception.LoanAppException;
import com.akotako.loanservice.model.response.ResponseObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LoanAppException.class)
    public ResponseEntity<ResponseObject<Void>> handleLoanAppException(LoanAppException ex) {
        return ResponseEntity.badRequest().body(ResponseObject.failure(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseObject<Void>> handleGeneralException(Exception ex) {
        return ResponseEntity.internalServerError().body(ResponseObject.failure("An unexpected error occurred: " + ex.getMessage()));
    }
}
