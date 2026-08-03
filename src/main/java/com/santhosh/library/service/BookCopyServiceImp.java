package com.santhosh.library.service;

import com.santhosh.library.dto.BookCopyResponse;
import com.santhosh.library.dto.CreateBookCopyRequest;
import com.santhosh.library.dto.UpdateBookCopyShelfRequest;
import com.santhosh.library.dto.UpdateBookCopyStatusRequest;
import com.santhosh.library.entity.Book;
import com.santhosh.library.entity.BookCopy;
import com.santhosh.library.entity.BookCopyStatus;
import com.santhosh.library.exception.BookCopyAlreadyExistsException;
import com.santhosh.library.exception.BookCopyNotFoundException;
import com.santhosh.library.exception.BookNotFoundException;
import com.santhosh.library.repository.BookCopyRepository;
import com.santhosh.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookCopyServiceImp implements BookCopyService {

    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;

    public BookCopyServiceImp(BookCopyRepository bookCopyRepository, BookRepository bookRepository){
        this.bookCopyRepository = bookCopyRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional
    public BookCopyResponse createBookCopy(CreateBookCopyRequest request, Long bookId){
        Book book = bookRepository.findByIdAndDeletedFalse(bookId).orElseThrow(() -> new BookNotFoundException("Book not found"));
        if(bookCopyRepository.existsByBarcode(request.getBarcode())){
            throw new BookCopyAlreadyExistsException("Book copy already exist");
        }

        BookCopy bookCopy = new BookCopy();
        bookCopy.setBarcode(request.getBarcode());
        bookCopy.setBook(book);
        bookCopy.setShelfNumber(request.getShelfNumber());

        bookCopy = bookCopyRepository.save(bookCopy);

        BookCopyResponse response = new BookCopyResponse();

        response.setId(bookCopy.getId());
        response.setBarcode(bookCopy.getBarcode());
        response.setBookId(book.getId());
        response.setShelfNumber(bookCopy.getShelfNumber());
        response.setStatus(bookCopy.getStatus());
        response.setTitle(book.getTitle());

        return response;
    }

    @Override
    public List<BookCopyResponse> getBookCopies(Long bookId) {

        Book book = bookRepository.findByIdAndDeletedFalse(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        List<BookCopy> bookCopies = bookCopyRepository.findByBookId(book.getId());

        List<BookCopyResponse> response = new ArrayList<>();

        for (BookCopy bookCopy : bookCopies) {

            BookCopyResponse copyResponse = new BookCopyResponse();

            copyResponse.setId(bookCopy.getId());
            copyResponse.setBookId(book.getId());
            copyResponse.setTitle(book.getTitle());
            copyResponse.setBarcode(bookCopy.getBarcode());
            copyResponse.setShelfNumber(bookCopy.getShelfNumber());
            copyResponse.setStatus(bookCopy.getStatus());

            response.add(copyResponse);
        }

        return response;
    }

    @Override
    public BookCopyResponse getBookCopy(Long id){
        BookCopy bookCopy = bookCopyRepository.findById(id).orElseThrow(() -> new BookCopyNotFoundException("Book copy not found"));
        BookCopyResponse response = new BookCopyResponse();

        response.setId(bookCopy.getId());
        response.setBookId(bookCopy.getBook().getId());
        response.setTitle(bookCopy.getBook().getTitle());
        response.setBarcode(bookCopy.getBarcode());
        response.setShelfNumber(bookCopy.getShelfNumber());
        response.setStatus(bookCopy.getStatus());

        return response;
    }

    @Override
    @Transactional
    public void updateBookCopyShelf(UpdateBookCopyShelfRequest request, Long id){
        BookCopy bookCopy = bookCopyRepository.findById(id).orElseThrow(() -> new BookCopyNotFoundException("Book copy not found"));
        bookCopy.setShelfNumber(request.getShelfNumber());
    }

    @Override
    @Transactional
    public void updateBookCopyStatus(UpdateBookCopyStatusRequest request, Long id){
        BookCopy bookCopy = bookCopyRepository.findById(id).orElseThrow(() -> new BookCopyNotFoundException("Book copy not found"));
        bookCopy.setStatus(request.getStatus());
    }

    @Override
    @Transactional
    public void deleteBookCopy(Long id){
        BookCopy bookCopy = bookCopyRepository.findById(id).orElseThrow(() -> new BookCopyNotFoundException("Book copy not found"));
        bookCopy.setStatus(BookCopyStatus.LOST);
    }
}
