package com.akotako.loanservice.model.exception;

public class LoanInstallmentNotFoundException extends LoanAppException {
    public LoanInstallmentNotFoundException(String message) {
        super(message);
    }
}
