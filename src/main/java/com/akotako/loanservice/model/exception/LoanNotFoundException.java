package com.akotako.loanservice.model.exception;

public class LoanNotFoundException extends LoanAppException {
    public LoanNotFoundException(String message) {
        super(message);
    }
}
