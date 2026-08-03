package com.santhosh.library.controller;

import com.santhosh.library.dto.CreateLoanRequest;
import com.santhosh.library.dto.LoanResponse;
import com.santhosh.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/loans")
public class LoanController {
    private final LoanService loanService;

    public LoanController(LoanService loanService){
        this.loanService = loanService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<LoanResponse> createLoan(@RequestBody @Valid CreateLoanRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.createLoan(request));
    }
}
