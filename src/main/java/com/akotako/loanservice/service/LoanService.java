package com.akotako.loanservice.service;

import com.akotako.loanservice.model.entity.Customer;
import com.akotako.loanservice.model.entity.Loan;
import com.akotako.loanservice.model.entity.LoanInstallment;
import com.akotako.loanservice.model.exception.*;
import com.akotako.loanservice.model.response.PayLoanResult;
import com.akotako.loanservice.repository.CustomerRepository;
import com.akotako.loanservice.repository.LoanInstallmentRepository;
import com.akotako.loanservice.repository.LoanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class LoanService {
    private final CustomerRepository customerRepository;
    private final LoanRepository loanRepository;
    private final LoanInstallmentRepository installmentRepository;

    public LoanService(CustomerRepository customerRepository, LoanRepository loanRepository,
                       LoanInstallmentRepository installmentRepository) {
        this.customerRepository = customerRepository;
        this.loanRepository = loanRepository;
        this.installmentRepository = installmentRepository;
    }

    @Transactional
    public Loan createLoan(Long customerId, Double amount, Double interestRate, Integer installments) {
        if (installments != 6 && installments != 9 && installments != 12 && installments != 24) {
            throw new InvalidLoanRequestException("Number of installments must be 6, 9, 12, or 24.");
        }
        if (interestRate < 0.1 || interestRate > 0.5) {
            throw new InvalidLoanRequestException("Interest rate must be between 0.1 and 0.5.");
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found."));

        Double totalAmount = amount * (1 + interestRate);
        if (customer.getUsedCreditLimit() + totalAmount > customer.getCreditLimit()) {
            throw new LoanLimitExceededException("Customer does not have enough credit limit.");
        }

        Loan loan = new Loan();
        loan.setCustomer(customer);
        loan.setLoanAmount(totalAmount);
        loan.setNumberOfInstallments(installments);
        loan.setInterestRate(interestRate);

        loanRepository.save(loan);

        List<LoanInstallment> installmentsList = new ArrayList<>();
        Double installmentAmount = totalAmount / installments;
        for (int i = 1; i <= installments; i++) {
            LoanInstallment installment = new LoanInstallment();
            installment.setLoan(loan);
            installment.setAmount(installmentAmount);
            installment.setDueDate(LocalDate.now().plusMonths(i).withDayOfMonth(1));

            installmentsList.add(installment);
        }
        installmentRepository.saveAll(installmentsList);

        customer.setUsedCreditLimit(customer.getUsedCreditLimit() + totalAmount);
        customerRepository.save(customer);

        return loan;
    }

    public List<Loan> listLoans(Long customerId) {
        return loanRepository.findByCustomerId(customerId);
    }

    public List<LoanInstallment> listInstallments(Long loanId) {
        return installmentRepository.findByLoanId(loanId);
    }

    @Transactional
    public PayLoanResult payLoan(Long loanId, Double paymentAmount) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found."));

        List<LoanInstallment> installments = installmentRepository
                .findByLoanIdAndIsPaidFalseAndDueDateBeforeOrderByDueDateAsc(loanId, LocalDate.now().plusMonths(3).withDayOfMonth(1));

        if (installments.isEmpty()) {
            throw new LoanInstallmentNotFoundException("No installments available for payment within the allowed period.");
        }

        double remainingAmount = paymentAmount;
        int paidInstallmentsCount = 0;
        double totalPaidAmount = 0.0;

        for (LoanInstallment installment : installments) {
            if (remainingAmount >= installment.getAmount()) {
                remainingAmount -= installment.getAmount();
                totalPaidAmount += installment.getAmount();
                installment.setPaidAmount(installment.getAmount());
                installment.setPaymentDate(LocalDate.now());
                installment.setIsPaid(true);
                installmentRepository.save(installment);
                paidInstallmentsCount++;
            } else {
                break;
            }
        }

        if (isAllInstallmentsPaid(loanId)) {
            loan.setIsPaid(true);
            loanRepository.save(loan);
        }

        return new PayLoanResult(paidInstallmentsCount, totalPaidAmount, loan.getIsPaid());
    }

    private boolean isAllInstallmentsPaid(Long loanId) {
        return installmentRepository.findByLoanId(loanId).stream().allMatch(LoanInstallment::getIsPaid);
    }

}
