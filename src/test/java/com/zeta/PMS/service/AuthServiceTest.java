package com.zeta.PMS.service;

import com.zeta.PMS.dto.AuthRequest;
import com.zeta.PMS.dto.AuthResponse;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.repository.UserRepository;
import com.zeta.PMS.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authRequest = new AuthRequest();
        authRequest.setEmail("test@example.com");
        authRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("hashedPassword");
    }

    @Test
    void authenticate_success() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn("fake-jwt-token");
        when(jwtUtil.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());
        assertEquals(3600L, response.getExpiresIn());

        verify(userRepository).findByEmailIgnoreCase("test@example.com");
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void authenticate_userNotFound() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(null);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
    }

    @Test
    void authenticate_invalidPassword() {
        when(userRepository.findByEmailIgnoreCase("test@example.com")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
    }
}
