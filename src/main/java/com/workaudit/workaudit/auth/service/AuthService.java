package com.workaudit.workaudit.auth.service;

import com.workaudit.workaudit.auth.dto.LoginRequest;
import com.workaudit.workaudit.auth.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}