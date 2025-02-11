package com.akotako.loanservice.controller;

import com.akotako.loanservice.model.entity.Loan;
import com.akotako.loanservice.model.entity.LoanInstallment;
import com.akotako.loanservice.model.request.CreateLoanRequest;
import com.akotako.loanservice.model.response.PayLoanResult;
import com.akotako.loanservice.model.response.ResponseObject;
import com.akotako.loanservice.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {
    private final LoanService loanService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseObject<Loan>> createLoan(@RequestBody @Valid CreateLoanRequest request) {
        Loan loan = loanService.createLoan(request);
        return ResponseEntity.ok(ResponseObject.success(loan));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    @GetMapping
    public ResponseEntity<ResponseObject<List<Loan>>> listLoans(
            @RequestParam(value = "customerId", required = false) Long customerId) {
        List<Loan> loans = loanService.listLoans(customerId);
        return ResponseEntity.ok(ResponseObject.success(loans));
    }

    @PreAuthorize("hasRole('ADMIN') or @loanService.isLoanOwner(#loanId, authentication.name)")
    @PostMapping("/pay")
    public ResponseEntity<ResponseObject<PayLoanResult>> payLoan(@RequestParam Long loanId,
                                                                 @RequestParam BigDecimal paymentAmount) {
        PayLoanResult result = loanService.payLoan(loanId, paymentAmount);
        return ResponseEntity.ok(ResponseObject.success(result));
    }

}
