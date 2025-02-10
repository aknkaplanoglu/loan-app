package com.akotako.loanservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private Double loanAmount;
    private Integer numberOfInstallments;
    private Double interestRate;
    private Boolean isPaid = false;

    private LocalDate createDate = LocalDate.now();
}
