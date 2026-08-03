package com.santhosh.library.service;

import com.santhosh.library.dto.CreateLoanRequest;
import com.santhosh.library.dto.LoanResponse;
import com.santhosh.library.dto.ReturnLoanRequest;

public interface LoanService {

    LoanResponse createLoan(CreateLoanRequest request);
    void returnLoan(ReturnLoanRequest request);

}
