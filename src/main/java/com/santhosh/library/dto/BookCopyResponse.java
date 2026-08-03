package com.santhosh.library.dto;

import com.santhosh.library.entity.BookCopyStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BookCopyResponse {

    private Long id;
    private Long bookId;
    private String title;
    private String barcode;
    private String shelfNumber;
    private BookCopyStatus status;
}
