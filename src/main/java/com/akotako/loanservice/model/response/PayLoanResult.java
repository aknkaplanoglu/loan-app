package com.akotako.loanservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PayLoanResult {
    private int paidInstallmentsCount;
    private double totalPaidAmount;
    private boolean isLoanFullyPaid;
}
