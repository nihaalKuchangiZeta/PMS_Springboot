package com.zeta.PMS.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    // wrappers so we can detect "not provided"
    private Double amount;      // nullable -> partial updates supported
    private String type;        // enum as String (case-insensitive)
    private String category;
    private String status;
    private String date;        // yyyy-MM-dd as String
    private Long createdBy;     // nullable
}

