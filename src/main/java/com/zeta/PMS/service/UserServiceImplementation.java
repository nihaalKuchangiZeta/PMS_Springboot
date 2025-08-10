package com.zeta.PMS.service;

import com.zeta.PMS.dto.UserRequest;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.enums.UserRole;
import com.zeta.PMS.exception.UserCreationException;
import com.zeta.PMS.repository.UserRepository;
import com.zeta.PMS.util.ValidationUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImplementation implements UserService{

    @Autowired
    private UserRepository repository;

    @Override
    @Transactional
    public User createUser(UserRequest request) {

        // Name validation
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new UserCreationException("Name cannot be blank");
        }
        if (request.getName().length() < 2 || request.getName().length() > 50) {
            throw new UserCreationException("Name must be between 2 and 50 characters");
        }

        // Email validation
        if (!ValidationUtil.isValidEmail(request.getEmail())) {
            throw new UserCreationException("Invalid email format");
        }
        if (repository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new UserCreationException("A user with this email already exists");
        }

        // Role validation and conversion
        UserRole role;
        try {
            role = ValidationUtil.parseEnumIgnoreCase(UserRole.class, request.getRole());
        } catch (IllegalArgumentException e) {
            throw new UserCreationException("Invalid role. Must be ADMIN, FINANCE_MANAGER, or VIEWER");
        }

        // Map DTO to Entity
        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim());
        user.setRole(role);

        // Save user
        try {
            return repository.save(user);
        } catch (Exception e) {
            throw new UserCreationException("Failed to create user: " + e.getMessage());
        }
    }


    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }
}
