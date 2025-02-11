package com.akotako.loanservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PayLoanResult {
    private int paidInstallmentsCount;
    private BigDecimal totalPaidAmount;
    private boolean isLoanFullyPaid;
}
