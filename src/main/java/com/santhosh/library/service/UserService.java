package com.santhosh.library.service;

import com.santhosh.library.dto.AuthResponse;
import com.santhosh.library.dto.LoginRequest;
import com.santhosh.library.dto.SignupRequest;

public interface UserService {

    AuthResponse signUp(SignupRequest request);
    AuthResponse login(LoginRequest request);

}
