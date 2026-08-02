package com.santhosh.library.service;

import com.santhosh.library.dto.BookResponse;
import com.santhosh.library.dto.CreateBookRequest;

public interface BookService {
    BookResponse createBook(CreateBookRequest request);
}
