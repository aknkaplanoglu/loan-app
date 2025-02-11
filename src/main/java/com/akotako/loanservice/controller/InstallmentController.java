package com.akotako.loanservice.controller;

import com.akotako.loanservice.model.entity.LoanInstallment;
import com.akotako.loanservice.model.response.ResponseObject;
import com.akotako.loanservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/installments")
@RequiredArgsConstructor
public class InstallmentController {
    private final LoanService loanService;

    @PreAuthorize("hasRole('ADMIN') or @loanService.isLoanOwner(#loanId, authentication.name)")
    @GetMapping
    public ResponseEntity<ResponseObject<List<LoanInstallment>>> listInstallments(
            @RequestParam(value = "loanId", required = false) Long loanId) {
        List<LoanInstallment> installments = loanService.listInstallments(loanId);
        return ResponseEntity.ok(ResponseObject.success(installments));
    }

}
