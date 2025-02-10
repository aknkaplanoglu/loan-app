package com.akotako.loanservice.repository;

import com.akotako.loanservice.model.entity.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LoanInstallmentRepository extends JpaRepository<LoanInstallment, Long> {
    List<LoanInstallment> findByLoanId(Long loanId);
    List<LoanInstallment> findByLoanIdAndIsPaidFalseAndDueDateBeforeOrderByDueDateAsc(Long loanId, LocalDate dueDate);
}
