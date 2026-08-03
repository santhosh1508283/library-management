package com.santhosh.library.dto;

import com.santhosh.library.entity.LoanStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LoanHistoryResponse {

    private Long loanId;

    private Long bookId;
    private String title;
    private String barcode;

    private LocalDateTime borrowedAt;
    private LocalDateTime dueDate;
    private LocalDateTime returnedAt;

    private LoanStatus status;

}