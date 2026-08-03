package com.santhosh.library.dto;

import com.santhosh.library.entity.LoanStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LoanResponse {

    private Long loanId;

    private Long bookId;

    private Long copyId;

    private String bookTitle;

    private String barcode;

    private LocalDateTime borrowedAt;

    private LocalDateTime dueDate;

    private LoanStatus status;

}