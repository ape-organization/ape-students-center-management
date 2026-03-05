package com.example.student.service;

import com.example.student.generated.model.AuthResponse;
import com.example.student.generated.model.LoginRequest;
import com.example.student.generated.model.RefreshTokenRequest;
import com.example.student.generated.model.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request);
}
