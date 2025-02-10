package com.akotako.loanservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "loan_installments")
public class LoanInstallment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan;

    private Double amount;
    private Double paidAmount = 0.0;

    private LocalDate dueDate;
    private LocalDate paymentDate;

    private Boolean isPaid = false;
}
