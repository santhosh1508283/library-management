package com.santhosh.library.service;

import com.santhosh.library.dto.AuthResponse;
import com.santhosh.library.dto.LoginRequest;
import com.santhosh.library.dto.SignupRequest;
import com.santhosh.library.entity.Role;
import com.santhosh.library.entity.User;
import com.santhosh.library.exception.EmailAlreadyExistsException;
import com.santhosh.library.exception.InvalidCredentialsException;
import com.santhosh.library.repository.UserRepository;
import com.santhosh.library.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse signUp(SignupRequest request){
        boolean userExist = userRepository.existsByEmail(request.getEmail());
        if(userExist){
            throw new EmailAlreadyExistsException("Email already exist");
        }
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.MEMBER);
        userRepository.save(user);
        AuthResponse response = new AuthResponse();
        response.setAccessToken(jwtService.generateAccessToken(user));
        response.setRefreshToken(jwtService.generateRefreshToken(user));
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        return response;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
        boolean isValidPassword = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!isValidPassword){
            throw new InvalidCredentialsException("Invalid email or password");
        }
        AuthResponse response = new AuthResponse();
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setAccessToken(jwtService.generateAccessToken(user));
        response.setRefreshToken(jwtService.generateRefreshToken(user));
        return response;
    }
}
