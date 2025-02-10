package com.akotako.loanservice.model.exception;

public class InvalidLoanRequestException extends LoanAppException {
    public InvalidLoanRequestException(String message) {
        super(message);
    }
}
