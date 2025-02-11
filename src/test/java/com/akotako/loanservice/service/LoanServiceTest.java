package com.akotako.loanservice.service;

import com.akotako.loanservice.model.entity.Customer;
import com.akotako.loanservice.model.entity.Loan;
import com.akotako.loanservice.model.entity.LoanInstallment;
import com.akotako.loanservice.model.exception.InvalidLoanRequestException;
import com.akotako.loanservice.model.exception.LoanInstallmentNotFoundException;
import com.akotako.loanservice.model.exception.LoanLimitExceededException;
import com.akotako.loanservice.model.request.CreateLoanRequest;
import com.akotako.loanservice.model.response.PayLoanResult;
import com.akotako.loanservice.repository.CustomerRepository;
import com.akotako.loanservice.repository.LoanInstallmentRepository;
import com.akotako.loanservice.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanInstallmentRepository installmentRepository;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(loanService, "rewardPenaltyConstant", BigDecimal.valueOf(0.001));
    }

    @Test
    void createLoan_shouldCreateLoanSuccessfully() {
        Long customerId = 1L;
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        createLoanRequest.setCustomerId(customerId);
        createLoanRequest.setAmount(BigDecimal.valueOf(10000));
        createLoanRequest.setInterestRate(0.2);
        createLoanRequest.setInstallments("12");

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setCreditLimit(BigDecimal.valueOf(20000));
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(installmentRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        Loan createdLoan = loanService.createLoan(createLoanRequest);

        assertNotNull(createdLoan);
        assertEquals(customerId, createdLoan.getCustomer().getId());
        assertTrue(BigDecimal.valueOf(12000).compareTo(createdLoan.getLoanAmount()) == 0);
        verify(customerRepository).save(customer);
    }

    @Test
    void createLoan_shouldThrowExceptionForExceedingCreditLimit() {
        Long customerId = 1L;
        CreateLoanRequest createLoanRequest = new CreateLoanRequest();
        createLoanRequest.setCustomerId(customerId);
        createLoanRequest.setAmount(BigDecimal.valueOf(20000.0));
        createLoanRequest.setInterestRate(0.2);
        createLoanRequest.setInstallments("12");

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setCreditLimit(BigDecimal.valueOf(15000));
        customer.setUsedCreditLimit(BigDecimal.ZERO);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        assertThrows(LoanLimitExceededException.class, () -> loanService.createLoan(createLoanRequest));
    }

    @Test
    void listLoans_shouldReturnLoansForCustomer() {
        Long customerId = 1L;

        Loan loan1 = new Loan();
        Loan loan2 = new Loan();
        List<Loan> loans = Arrays.asList(loan1, loan2);

        when(loanRepository.findByCustomerId(customerId)).thenReturn(loans);

        List<Loan> result = loanService.listLoans(customerId);

        assertEquals(2, result.size());
        verify(loanRepository).findByCustomerId(customerId);
    }

    @Test
    void listInstallments_shouldReturnInstallmentsForLoan() {
        Long loanId = 1L;

        LoanInstallment installment1 = new LoanInstallment();
        LoanInstallment installment2 = new LoanInstallment();
        List<LoanInstallment> installments = Arrays.asList(installment1, installment2);

        when(installmentRepository.findByLoanId(loanId)).thenReturn(installments);

        List<LoanInstallment> result = loanService.listInstallments(loanId);

        assertEquals(2, result.size());
        verify(installmentRepository).findByLoanId(loanId);
    }

    @Test
    void payLoan_shouldPayInstallmentsSuccessfully() {
        Long loanId = 1L;
        BigDecimal paymentAmount = BigDecimal.valueOf(1000);

        Loan loan = new Loan();
        loan.setId(loanId);
        loan.setIsPaid(false);

        LoanInstallment installment1 = new LoanInstallment();
        installment1.setAmount(BigDecimal.valueOf(500));
        installment1.setIsPaid(false);
        installment1.setDueDate(LocalDate.now().minusDays(10));

        LoanInstallment installment2 = new LoanInstallment();
        installment2.setAmount(BigDecimal.valueOf(500));
        installment2.setIsPaid(false);
        installment2.setDueDate(LocalDate.now().plusDays(10));

        List<LoanInstallment> installments = Arrays.asList(installment1, installment2);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(installmentRepository.findByLoanIdAndIsPaidFalseAndDueDateBeforeOrderByDueDateAsc(eq(loanId), any(LocalDate.class)))
                .thenReturn(installments);

        PayLoanResult result = loanService.payLoan(loanId, paymentAmount);

        assertEquals(2, result.getPaidInstallmentsCount());
        assertTrue(BigDecimal.valueOf(1000).compareTo(result.getTotalPaidAmount()) == 0);
        assertTrue(result.isLoanFullyPaid());
        verify(installmentRepository, times(2)).save(any(LoanInstallment.class));
    }

    @Test
    void payLoan_shouldThrowExceptionWhenNoInstallmentsAvailable() {
        Long loanId = 1L;
        BigDecimal paymentAmount = BigDecimal.valueOf(1000);

        Loan loan = new Loan();
        loan.setId(loanId);

        when(loanRepository.findById(loanId)).thenReturn(Optional.of(loan));
        when(installmentRepository.findByLoanIdAndIsPaidFalseAndDueDateBeforeOrderByDueDateAsc(eq(loanId), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(LoanInstallmentNotFoundException.class, () -> loanService.payLoan(loanId, paymentAmount));
    }
}

