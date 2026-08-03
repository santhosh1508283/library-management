package com.santhosh.library.controller;

import com.santhosh.library.dto.BookCopyResponse;
import com.santhosh.library.dto.CreateBookCopyRequest;
import com.santhosh.library.dto.UpdateBookCopyShelfRequest;
import com.santhosh.library.dto.UpdateBookCopyStatusRequest;
import com.santhosh.library.service.BookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService){
        this.bookCopyService = bookCopyService;
    }

    @PostMapping("/books/{bookId}/copies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<BookCopyResponse> createBookCopy(@RequestBody @Valid CreateBookCopyRequest request, @PathVariable Long bookId){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCopyService.createBookCopy(request, bookId));
    }

    @GetMapping("/books/{bookId}/copies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<BookCopyResponse>> getBookCopies(@PathVariable Long bookId){
        return ResponseEntity.ok(bookCopyService.getBookCopies(bookId));
    }

    @GetMapping("/book-copies/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<BookCopyResponse> getBookCopy(@PathVariable Long id){
        return ResponseEntity.ok(bookCopyService.getBookCopy(id));
    }

    @PatchMapping("/book-copies/{id}/shelf")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> updateBookCopyShelf(@RequestBody @Valid UpdateBookCopyShelfRequest request, @PathVariable Long id){
        bookCopyService.updateBookCopyShelf(request, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/book-copies/{id}/status")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> updateBookCopyStatus(@RequestBody @Valid UpdateBookCopyStatusRequest request, @PathVariable Long id){
        bookCopyService.updateBookCopyStatus(request, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/book-copies/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<Void> deleteBookCopy(@PathVariable Long id){
        bookCopyService.deleteBookCopy(id);
        return ResponseEntity.noContent().build();
    }
}
