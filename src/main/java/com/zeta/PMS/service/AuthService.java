package com.zeta.PMS.service;

import com.zeta.PMS.dto.AuthRequest;
import com.zeta.PMS.dto.AuthResponse;
import com.zeta.PMS.entity.User;
import com.zeta.PMS.repository.UserRepository;
import com.zeta.PMS.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse authenticate(AuthRequest req) {
        log.debug("Authenticating user with email: {}", req.getEmail());
        User user = userRepository.findByEmailIgnoreCase(req.getEmail());

        if (user == null) {
            log.warn("Authentication failed: user with email {} not found", req.getEmail());
            throw new BadCredentialsException("Invalid credentials");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: invalid password for email {}", req.getEmail());
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user);
        log.info("Authentication successful for email: {}", req.getEmail());

        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setExpiresIn(jwtUtil.getExpirationSeconds());
        return resp;
    }
}
