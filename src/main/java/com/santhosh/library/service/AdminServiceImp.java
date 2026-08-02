package com.santhosh.library.service;

import com.santhosh.library.dto.UpdateRoleRequest;
import com.santhosh.library.entity.User;
import com.santhosh.library.exception.InvalidCredentialsException;
import com.santhosh.library.exception.UserNotFoundException;
import com.santhosh.library.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImp implements AdminService {

    private final UserRepository userRepository;

    public AdminServiceImp(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void updateRole(UpdateRoleRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("Please enter valid email"));
        if (user.getRole() == request.getRole()) {
            return;
        }
        user.setRole(request.getRole());
    }
}
