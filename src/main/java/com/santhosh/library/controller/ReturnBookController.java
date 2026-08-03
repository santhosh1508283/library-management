package com.santhosh.library.controller;

import com.santhosh.library.dto.ReturnBookRequest;
import com.santhosh.library.service.ReturnService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/returns")
public class ReturnBookController {

    private final ReturnService returnService;

    public ReturnBookController(ReturnService returnService){
        this.returnService = returnService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'ADMIN')")
    ResponseEntity<Void> returnBook(@RequestBody @Valid ReturnBookRequest request){
        returnService.returnBook(request);
        return ResponseEntity.noContent().build();
    }
}
