package com.akotako.loanservice.controller;

import com.akotako.loanservice.model.entity.Loan;
import com.akotako.loanservice.model.entity.LoanInstallment;
import com.akotako.loanservice.model.response.PayLoanResult;
import com.akotako.loanservice.model.response.ResponseObject;
import com.akotako.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;


    @PostMapping
    public ResponseEntity<ResponseObject<Loan>> createLoan(@RequestParam Long customerId,
                                                           @RequestParam Double amount,
                                                           @RequestParam Double interestRate,
                                                           @RequestParam Integer installments) {
        Loan loan = loanService.createLoan(customerId, amount, interestRate, installments);
        return ResponseEntity.ok(ResponseObject.success(loan));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ResponseObject<List<Loan>>> listLoans(@PathVariable Long customerId) {
        List<Loan> loans = loanService.listLoans(customerId);
        return ResponseEntity.ok(ResponseObject.success(loans));
    }

    @GetMapping("/installments/{loanId}")
    public ResponseEntity<ResponseObject<List<LoanInstallment>>> listInstallments(@PathVariable Long loanId) {
        List<LoanInstallment> installments = loanService.listInstallments(loanId);
        return ResponseEntity.ok(ResponseObject.success(installments));
    }

    @PostMapping("/pay")
    public ResponseEntity<ResponseObject<PayLoanResult>> payLoan(@RequestParam Long loanId,
                                                                 @RequestParam Double paymentAmount) {
        PayLoanResult result = loanService.payLoan(loanId, paymentAmount);
        return ResponseEntity.ok(ResponseObject.success(result));
    }

}
