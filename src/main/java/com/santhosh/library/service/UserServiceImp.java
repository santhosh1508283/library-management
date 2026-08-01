package com.santhosh.library.service;

import com.santhosh.library.dto.AuthResponse;
import com.santhosh.library.dto.SignupRequest;
import com.santhosh.library.entity.Role;
import com.santhosh.library.entity.User;
import com.santhosh.library.exception.EmailAlreadyExistsException;
import com.santhosh.library.repository.UserRepository;
import com.santhosh.library.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    @Transactional
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
}
