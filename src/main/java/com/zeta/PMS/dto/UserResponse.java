package com.zeta.PMS.dto;

import com.zeta.PMS.entity.Payment;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
    private List<Payment> payments;

}
