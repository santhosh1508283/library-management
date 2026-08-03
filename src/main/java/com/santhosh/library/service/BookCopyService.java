package com.santhosh.library.service;

import com.santhosh.library.dto.BookCopyResponse;
import com.santhosh.library.dto.CreateBookCopyRequest;

import java.util.List;

public interface BookCopyService {

    BookCopyResponse createBookCopy(CreateBookCopyRequest request, Long bookId);
    List<BookCopyResponse> getBookCopies(Long bookId);

}
