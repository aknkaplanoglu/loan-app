package com.akotako.loanservice.model.exception;

public class LoanAppException extends RuntimeException {
    public LoanAppException(String message) {
        super(message);
    }
}
