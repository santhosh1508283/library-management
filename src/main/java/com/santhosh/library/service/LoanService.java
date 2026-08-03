package com.santhosh.library.service;

import com.santhosh.library.dto.*;

import java.util.List;

public interface LoanService {

    LoanResponse createLoan(CreateLoanRequest request);
    void returnLoan(ReturnLoanRequest request);
    List<ActiveLoanResponse> getActiveLoans();
    List<LoanHistoryResponse> getLoanHistory();

}
