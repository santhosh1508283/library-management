package com.santhosh.library.service;

import com.santhosh.library.dto.AuthResponse;
import com.santhosh.library.dto.LoginRequest;
import com.santhosh.library.dto.RefreshTokenRequest;
import com.santhosh.library.dto.SignupRequest;
import com.santhosh.library.entity.RefreshToken;
import com.santhosh.library.entity.Role;
import com.santhosh.library.entity.User;
import com.santhosh.library.exception.EmailAlreadyExistsException;
import com.santhosh.library.exception.InvalidCredentialsException;
import com.santhosh.library.repository.RefreshTokenRepository;
import com.santhosh.library.repository.UserRepository;
import com.santhosh.library.security.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserServiceImp(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    @Transactional
    public AuthResponse signUp(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exist");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
        System.out.println(passwordEncoder.encode(request.getPassword()));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        AuthResponse response = new AuthResponse();

        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        saveRefreshToken(user, refreshToken);

        return response;
    }

    private void saveRefreshToken(User user, String refreshTokenValue) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setDeviceId("web");
        refreshToken.setExpiresAt(
                jwtService.extractExpiration(refreshTokenValue)
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime()
        );

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String refreshTokenValue = request.getRefreshToken();

        RefreshToken storedRefreshToken = validateRefreshToken(refreshTokenValue);

        User user = storedRefreshToken.getUser();

        AuthResponse response = new AuthResponse();

        response.setRefreshToken(refreshTokenValue);
        response.setAccessToken(jwtService.generateAccessToken(user));
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        storedRefreshToken.setLastUsedAt(LocalDateTime.now());

        return response;
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {

        RefreshToken storedRefreshToken =
                validateRefreshToken(request.getRefreshToken());

        storedRefreshToken.setRevoked(true);
    }

    private RefreshToken validateRefreshToken(String refreshTokenValue) {

        RefreshToken refreshToken = refreshTokenRepository
                .findByRefreshToken(refreshTokenValue)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }
        User user = refreshToken.getUser();

        if (!jwtService.isRefreshTokenValid(refreshTokenValue, user)) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        return refreshToken;
    }
}
