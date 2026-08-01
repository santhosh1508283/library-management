package com.santhosh.library.controller;

import com.santhosh.library.dto.AuthResponse;
import com.santhosh.library.dto.SignupRequest;
import com.santhosh.library.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signUp(@RequestBody @Valid SignupRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(request));
    }
}
