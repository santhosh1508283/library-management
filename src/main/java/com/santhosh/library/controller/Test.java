package com.santhosh.library.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
public class Test {
    @GetMapping("/test")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public String signUp(){
        return "Hello Sourabha";
    }

    @GetMapping("/test-2")
    @PreAuthorize("hasRole('MEMBER')")
    public String test() {
        return "Success";
    }
}
