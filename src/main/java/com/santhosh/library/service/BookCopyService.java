package com.santhosh.library.service;

import com.santhosh.library.dto.BookCopyResponse;
import com.santhosh.library.dto.CreateBookCopyRequest;

public interface BookCopyService {

    BookCopyResponse createBookCopy(CreateBookCopyRequest request);
}
