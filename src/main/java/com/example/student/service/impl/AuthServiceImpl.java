package com.example.student.service.impl;

import com.example.student.generated.model.AuthResponse;
import com.example.student.generated.model.LoginRequest;
import com.example.student.generated.model.RefreshTokenRequest;
import com.example.student.generated.model.RegisterRequest;
import com.example.student.model.Role;
import com.example.student.model.User;
import com.example.student.repository.UserRepository;
import com.example.student.security.JwtTokenProvider;
import com.example.student.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider jwtTokenProvider,
                           UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists with this email");
        }

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Resolve role; default to USER if not provided
        Role assignedRole = Role.USER;
        if (request.getRole() != null) {
            try {
                assignedRole = Role.valueOf(request.getRole().getValue());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role: " + request.getRole());
            }
        }
        user.setRole(assignedRole);

        User savedUser = userRepository.save(user);

        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        AuthResponse resp = new AuthResponse();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setExpiresIn(jwtExpirationInMs / 1000);
        resp.setTokenType("Bearer");
        resp.setMessage("User registered successfully");
        return resp;
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Load user details
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        AuthResponse resp = new AuthResponse();
        resp.setAccessToken(accessToken);
        resp.setRefreshToken(refreshToken);
        resp.setExpiresIn(jwtExpirationInMs / 1000);
        resp.setTokenType("Bearer");
        resp.setMessage("Login successful");
        return resp;
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is required");
        }

        try {
            // Extract username from refresh token
            String username = jwtTokenProvider.extractUsername(refreshToken);

            // Validate token with username
            if (!jwtTokenProvider.isTokenValid(refreshToken, username)) {
                throw new RuntimeException("Invalid or expired refresh token");
            }

            // Load user details and generate new access token
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtTokenProvider.generateAccessToken(userDetails);

            AuthResponse resp = new AuthResponse();
            resp.setAccessToken(newAccessToken);
            resp.setRefreshToken(refreshToken);
            resp.setExpiresIn(jwtExpirationInMs / 1000);
            resp.setTokenType("Bearer");
            resp.setMessage("Token refreshed successfully");
            return resp;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to refresh token: " + ex.getMessage());
        }
    }
}
