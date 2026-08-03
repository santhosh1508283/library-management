package com.santhosh.library.controller;

import com.santhosh.library.dto.ActiveLoanResponse;
import com.santhosh.library.dto.CreateLoanRequest;
import com.santhosh.library.dto.LoanResponse;
import com.santhosh.library.dto.ReturnLoanRequest;
import com.santhosh.library.service.LoanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    ResponseEntity<Void> returnBook(@RequestBody @Valid ReturnLoanRequest request){
        loanService.returnLoan(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('MEMBER')")
    ResponseEntity<List<ActiveLoanResponse>> getActiveLoans(){
        return ResponseEntity.ok(loanService.getActiveLoans());
    }
}
