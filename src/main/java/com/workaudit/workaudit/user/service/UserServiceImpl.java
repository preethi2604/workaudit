package com.workaudit.workaudit.user.service;

import com.workaudit.workaudit.role.entity.Role;
import com.workaudit.workaudit.role.repository.RoleRepository;
import com.workaudit.workaudit.user.dto.UserRequest;
import com.workaudit.workaudit.user.dto.UserResponse;
import com.workaudit.workaudit.user.entity.User;
import com.workaudit.workaudit.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Create user
    @Override
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Role not found"));

        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        user.setRole(role);
        user.setActive(true);

        User savedUser = userRepository.save(user);

        return convertToResponse(savedUser);
    }

    // Get all users
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    // Get user by ID
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        return convertToResponse(user);
    }

    // Convert entity to response DTO
    private UserResponse convertToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setRoleId(user.getRole().getId());
        response.setRoleName(user.getRole().getName());
        response.setActive(user.getActive());

        return response;
    }
}