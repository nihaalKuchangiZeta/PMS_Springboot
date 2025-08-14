package com.zeta.PMS.service;

import com.zeta.PMS.dto.UserRequest;
import com.zeta.PMS.dto.UserResponse;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.enums.UserRole;
import com.zeta.PMS.exception.UserCreationException;
import com.zeta.PMS.repository.UserRepository;
import com.zeta.PMS.util.PasswordUtilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplementationTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordUtilService passwordUtilService;

    @InjectMocks
    private UserServiceImplementation service;

    private UserRequest request;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new UserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setRole("ADMIN");
        request.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setRole(UserRole.ADMIN);
        user.setPassword("hashedPassword");
    }

    @Test
    void createUser_success() {
        when(repository.existsByEmailIgnoreCase("john@example.com")).thenReturn(false);
        when(passwordUtilService.hashPassword("password123")).thenReturn("hashedPassword");
        when(repository.findByEmailIgnoreCase("john@example.com")).thenReturn(user);

        UserResponse response = service.createUser(request);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        verify(repository).save(any(User.class));
    }

    @Test
    void createUser_emailExists_throwsException() {
        when(repository.existsByEmailIgnoreCase("john@example.com")).thenReturn(true);
        assertThrows(UserCreationException.class, () -> service.createUser(request));
    }

    @Test
    void getAllUsers_returnsList() {
        when(repository.findAll()).thenReturn(List.of(user));

        List<UserResponse> responses = service.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals("John Doe", responses.get(0).getName());
    }
}
