package com.santhosh.library.service;

import com.santhosh.library.dto.ActiveLoanResponse;
import com.santhosh.library.dto.CreateLoanRequest;
import com.santhosh.library.dto.LoanResponse;
import com.santhosh.library.dto.ReturnLoanRequest;

import java.util.List;

public interface LoanService {

    LoanResponse createLoan(CreateLoanRequest request);
    void returnLoan(ReturnLoanRequest request);
    List<ActiveLoanResponse> getActiveLoans();

}
