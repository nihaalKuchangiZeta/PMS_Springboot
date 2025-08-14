package com.zeta.PMS.service;

import com.zeta.PMS.dto.UserRequest;
import com.zeta.PMS.dto.UserResponse;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.enums.UserRole;
import com.zeta.PMS.exception.UserCreationException;
import com.zeta.PMS.repository.UserRepository;
import com.zeta.PMS.util.PasswordUtilService;
import com.zeta.PMS.util.ValidationUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserServiceImplementation implements UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordUtilService passwordUtilService;

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {
        log.debug("Attempting to create user with email: {}", request.getEmail());

        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            log.warn("User creation failed - email {} already exists", request.getEmail());
            throw new UserCreationException("A user with this email already exists");
        }

        UserRole role;
        try {
            role = ValidationUtil.parseEnum(UserRole.class, request.getRole());
        } catch (IllegalArgumentException e) {
            log.error("Invalid role provided: {}", request.getRole());
            throw new UserCreationException(e.getMessage() + ". Must be ADMIN, FINANCE_MANAGER, or VIEWER");
        }

        String hashedPassword = passwordUtilService.hashPassword(request.getPassword());

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setRole(role);
        user.setPassword(hashedPassword);

        try {
            repository.save(user);
            User savedUser = repository.findByEmailIgnoreCase(user.getEmail());
            log.info("User created successfully with ID: {}", savedUser.getId());
            return convertToUserResponse(savedUser);
        } catch (Exception e) {
            log.error("Error while creating user: {}", e.getMessage(), e);
            throw new UserCreationException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Retrieving all users from database");
        List<UserResponse> users = repository.findAll()
                .stream()
                .map(this::convertToUserResponse)
                .toList();
        log.info("Retrieved {} users", users.size());
        return users;
    }

    private UserResponse convertToUserResponse(User user) {
        UserResponse dto = new UserResponse();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRole(String.valueOf(user.getRole()));
        dto.setPayments(user.getPayments());
        return dto;
    }
}
