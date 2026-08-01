package com.santhosh.library.service;

import com.santhosh.library.dto.AuthResponse;
import com.santhosh.library.dto.SignupRequest;
import com.santhosh.library.entity.Role;
import com.santhosh.library.entity.User;
import com.santhosh.library.exception.EmailAlreadyExistsException;
import com.santhosh.library.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AuthResponse signUp(SignupRequest request){
        boolean userExist = userRepository.userExistsByEmail(request.getEmail());
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
        response.setAccessToken("access_token");
        response.setRefreshToken("refresh_token");
        response.setEmail(user.getEmail());
        response.setName(user.getName());
        response.setRole(user.getRole());
        return response;
    }
}
