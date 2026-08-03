package com.santhosh.library.controller;

import com.santhosh.library.dto.BookCopyResponse;
import com.santhosh.library.dto.CreateBookCopyRequest;
import com.santhosh.library.service.BookCopyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookCopyController {

    private final BookCopyService bookCopyService;

    public BookCopyController(BookCopyService bookCopyService){
        this.bookCopyService = bookCopyService;
    }

    @PostMapping("/{bookId}/copies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<BookCopyResponse> createBookCopy(@RequestBody @Valid CreateBookCopyRequest request, @PathVariable Long bookId){
        return ResponseEntity.status(HttpStatus.CREATED).body(bookCopyService.createBookCopy(request, bookId));
    }

    @GetMapping("/{bookId}/copies")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    public ResponseEntity<List<BookCopyResponse>> getBookCopies(@PathVariable Long bookId){
        return ResponseEntity.ok(bookCopyService.getBookCopies(bookId));
    }
}
