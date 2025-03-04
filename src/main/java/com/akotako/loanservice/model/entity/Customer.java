package com.akotako.loanservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "customers")
public class Customer extends BaseEntity {

    private String name;
    private String surname;

    @Column(unique = true, nullable = false)
    private String username;

    private BigDecimal creditLimit;
    private BigDecimal usedCreditLimit = BigDecimal.ZERO;
}
