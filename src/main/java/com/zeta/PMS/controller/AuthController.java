package com.zeta.PMS.controller;

import com.zeta.PMS.dto.AuthRequest;
import com.zeta.PMS.dto.AuthResponse;
import com.zeta.PMS.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        AuthResponse resp = authService.authenticate(request);
        log.info("User authenticated successfully for email: {}", request.getEmail());
        return ResponseEntity.ok(resp);
    }
}
