package com.zeta.PMS.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private long expiresIn; // seconds
}
