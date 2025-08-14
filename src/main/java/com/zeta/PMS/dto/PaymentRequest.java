package com.zeta.PMS.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PaymentRequest {
    // wrappers so we can detect "not provided"
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private Double amount;
    private String type;
    private String category;
    private String status;
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in yyyy-MM-dd format")
    private String date;
}

