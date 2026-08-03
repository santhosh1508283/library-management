package com.santhosh.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ActiveLoanResponse {

    private Long loanId;
    private Long bookId;
    private String title;
    private String barcode;
    private LocalDateTime borrowedAt;
    private LocalDateTime dueDate;

}