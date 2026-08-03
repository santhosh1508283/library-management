package com.santhosh.library.service;

import com.santhosh.library.dto.BookCopyResponse;
import com.santhosh.library.dto.CreateBookCopyRequest;
import com.santhosh.library.dto.UpdateBookCopyShelfRequest;
import com.santhosh.library.dto.UpdateBookCopyStatusRequest;

import java.util.List;

public interface BookCopyService {

    BookCopyResponse createBookCopy(CreateBookCopyRequest request, Long bookId);
    List<BookCopyResponse> getBookCopies(Long bookId);
    BookCopyResponse getBookCopy(Long id);
    void updateBookCopyShelf(UpdateBookCopyShelfRequest request, Long id);
    void updateBookCopyStatus(UpdateBookCopyStatusRequest request, Long id);
    void deleteBookCopy(Long id);

}
