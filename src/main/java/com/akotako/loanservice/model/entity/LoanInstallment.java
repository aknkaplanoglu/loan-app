package com.akotako.loanservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "loan_installments")
public class LoanInstallment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    private BigDecimal amount;
    private BigDecimal paidAmount = BigDecimal.ZERO;

    private LocalDate dueDate;
    private LocalDate paymentDate;

    private Boolean isPaid = false;
}
