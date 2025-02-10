package com.akotako.loanservice.model.exception;

public class CustomerNotFoundException extends LoanAppException {
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
