package com.akotako.loanservice.service;

import com.akotako.loanservice.model.entity.Customer;
import com.akotako.loanservice.model.entity.Loan;
import com.akotako.loanservice.model.entity.LoanInstallment;
import com.akotako.loanservice.model.exception.*;
import com.akotako.loanservice.model.request.CreateLoanRequest;
import com.akotako.loanservice.model.response.PayLoanResult;
import com.akotako.loanservice.repository.CustomerRepository;
import com.akotako.loanservice.repository.LoanInstallmentRepository;
import com.akotako.loanservice.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoanService {

    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;
    @Value("${loan.reward-penalty.constant:0.001}")
    private BigDecimal rewardPenaltyConstant;


    @Transactional
    public Loan createLoan(CreateLoanRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found."));

        BigDecimal totalAmount = request.getAmount().multiply(BigDecimal.valueOf(1 + request.getInterestRate()));
        if (customer.getUsedCreditLimit().add(totalAmount).compareTo(customer.getCreditLimit()) > 0) {
            throw new LoanLimitExceededException("Customer does not have enough credit limit.");
        }

        int installments = Integer.parseInt(request.getInstallments());
        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(totalAmount);
        loan.setNumberOfInstallments(installments);
        loan.setInterestRate(request.getInterestRate());

        loanRepository.save(loan);

        populateInstallments(totalAmount, installments, loan);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit().add(totalAmount));
        customerRepository.save(customer);
        log.info("Created loan with credit limit {} for customer: {}", customer.getCreditLimit(), request.getCustomerId());

        return loan;
    }

    private void populateInstallments(BigDecimal totalAmount, int installments, Loan loan) {
        List<LoanInstallment> installmentsList = new ArrayList<>();
        BigDecimal installmentAmount = totalAmount.divide(
                BigDecimal.valueOf(installments),
                2,
                RoundingMode.HALF_UP
        );
        for (int i = 1; i <= installments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setDueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1));

            installmentsList.add(installment);
        }
        installmentRepository.saveAll(installmentsList);
    }

    public List<Loan> listLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<LoanInstallment> listInstallments(Long loanId) {
        return installmentRepository.findByLoanId(loanId);
    }

    public boolean isLoanOwner(Long loanId, String username) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanAppException("Loan not found."));
        Customer customer = loan.getCustomer();
        return customer.getUsername().equals(username);
    }

    @Transactional
    public PayLoanResult payLoan(Long loanId, BigDecimal paymentAmount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found."));

        List<LoanInstallment> installments = installmentRepository.findByLoanIdAndIsPaidFalseAndDueDateBeforeOrderByDueDateAsc(
                loanId, LocalDate.now().plusMonths(3).withDayOfMonth(2));

        if (installments.isEmpty()) {
            throw new LoanInstallmentNotFoundException("No installments available for payment within the allowed period.");
        }

        BigDecimal remainingAmount = paymentAmount;
        int paidInstallmentsCount = 0;
        BigDecimal totalPaidAmount = BigDecimal.ZERO;

        for (LoanInstallment installment : installments) {
            BigDecimal adjustedAmount = installment.getAmount();
            adjustedAmount = recalculatePaymentAmount(installment, adjustedAmount);

            if (remainingAmount.compareTo(adjustedAmount) >= 0) {
                remainingAmount = remainingAmount.subtract(adjustedAmount);
                totalPaidAmount = totalPaidAmount.add(adjustedAmount);
                installment.setPaidAmount(adjustedAmount);
                installment.setPaymentDate(LocalDate.now());
                installment.setIsPaid(true);
                installmentRepository.save(installment);
                paidInstallmentsCount++;
            } else {
                break;
            }
        }

        // can be async
        if (isAllInstallmentsPaid(loanId)) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        return new PayLoanResult(paidInstallmentsCount, totalPaidAmount, loan.getIsPaid());
    }

    private BigDecimal recalculatePaymentAmount(LoanInstallment installment, BigDecimal adjustedAmount) {
        long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), installment.getDueDate());

        if (daysDifference > 0) {
            adjustedAmount = adjustedAmount.subtract(installment.getAmount().multiply(rewardPenaltyConstant).multiply(BigDecimal.valueOf(daysDifference))); // early
        } else if (daysDifference < 0) {
            adjustedAmount = adjustedAmount.add(installment.getAmount().multiply(rewardPenaltyConstant).multiply(BigDecimal.valueOf(Math.abs(daysDifference)))); // late
        }
        return adjustedAmount;
    }


    private boolean isAllInstallmentsPaid(Long loanId) {
        return installmentRepository.findByLoanId(loanId).stream().allMatch(LoanInstallment::getIsPaid);
    }

}
