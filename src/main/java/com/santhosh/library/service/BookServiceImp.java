package com.santhosh.library.service;

import com.santhosh.library.dto.BookResponse;
import com.santhosh.library.dto.CreateBookRequest;
import com.santhosh.library.dto.UpdateBookRequest;
import com.santhosh.library.entity.Book;
import com.santhosh.library.exception.BookAlreadyExistsException;
import com.santhosh.library.exception.BookNotFoundException;
import com.santhosh.library.repository.BookRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookServiceImp implements BookService{

    private final BookRepository bookRepository;

    public BookServiceImp(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Transactional
    public BookResponse createBook(CreateBookRequest request){
        if(bookRepository.existsByIsbn(request.getIsbn())){
            throw new BookAlreadyExistsException("Book with this ISBN already exists");
        }
        Book book = new Book();
        book.setAuthor(request.getAuthor());
        book.setEdition(request.getEdition());
        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());

        book = bookRepository.save(book);

        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setAuthor(book.getAuthor());
        response.setEdition(book.getEdition());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());


        return response;
    }

    @Override
    public List<BookResponse> getAllBooks(){
        List<Book> books = bookRepository.findByDeletedFalse();
        List<BookResponse> resultBooks = new ArrayList<>();

        for (Book book : books) {
            BookResponse response = new BookResponse();

            response.setId(book.getId());
            response.setTitle(book.getTitle());
            response.setAuthor(book.getAuthor());
            response.setEdition(book.getEdition());
            response.setIsbn(book.getIsbn());

            resultBooks.add(response);
        }

        return resultBooks;
    }

    @Override
    public BookResponse getBookById(Long id){
        Book book = bookRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new BookNotFoundException("Book not found"));
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setEdition(book.getEdition());
        return response;
    }

    @Override
    @Transactional
    public BookResponse updateBook(UpdateBookRequest request, Long id){
        Book book = bookRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BookNotFoundException("Book not found"));

        if (!book.getIsbn().equals(request.getIsbn())
                && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BookAlreadyExistsException("Book with same ISBN exists");
        }

        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setEdition(request.getEdition());
        book.setIsbn(request.getIsbn());

        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setEdition(book.getEdition());
        response.setIsbn(book.getIsbn());

        return response;
    }
    @Override
    @Transactional
    public void deleteBook(Long id){
        Book book = bookRepository.findByIdAndDeletedFalse(id).orElseThrow(() -> new BookNotFoundException("Book does not exist"));
        book.setDeleted(true);
        book.setDeletedAt(LocalDateTime.now());
    }
}
