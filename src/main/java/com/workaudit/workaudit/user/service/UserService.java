package com.workaudit.workaudit.user.service;

import com.workaudit.workaudit.user.dto.UserRequest;
import com.workaudit.workaudit.user.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}