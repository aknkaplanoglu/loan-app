package com.akotako.loanservice.model.exception;

public class UserNotFoundException extends LoanAppException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
