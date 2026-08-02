package com.santhosh.library.service;

import com.santhosh.library.dto.BookResponse;
import com.santhosh.library.dto.CreateBookRequest;

import java.util.List;

public interface BookService {
    BookResponse createBook(CreateBookRequest request);
    List<BookResponse> getAllBooks();
    BookResponse getBookById(Long id);
    BookResponse updateBook(CreateBookRequest request, Long id);
    void deleteBook(Long id);
}
