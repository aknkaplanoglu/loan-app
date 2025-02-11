package com.akotako.loanservice.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateLoanRequest {

    @NotNull(message = "Customer ID cannot be null.")
    private Long customerId;

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Interest rate cannot be null.")
    @DecimalMin(value = "0.1", message = "Interest rate must be at least 0.1.")
    @DecimalMax(value = "0.5", message = "Interest rate must be at most 0.5.")
    private Double interestRate;

    @NotNull(message = "Number of installments cannot be null.")
    @Pattern(regexp = "6|9|12|24", message = "Number of installments must be 6, 9, 12, or 24.")
    private String installments;
}
