package com.santhosh.library.service;

import com.santhosh.library.dto.CreateLoanRequest;
import com.santhosh.library.dto.LoanResponse;

public interface LoanService {

    LoanResponse createLoan(CreateLoanRequest request);

}
