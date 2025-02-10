package com.akotako.loanservice.model.exception;

public class LoanLimitExceededException extends LoanAppException {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
