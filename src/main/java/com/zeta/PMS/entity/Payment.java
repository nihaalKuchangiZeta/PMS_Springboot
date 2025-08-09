package com.zeta.PMS.entity;

import jakarta.persistence.*;
import lombok.Data;

enum PaymentType {
    INCOMING,
    OUTGOING
}

enum PaymentCategory {
    SALARY,
    VENDOR,
    INVOICE
}

enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED
}


@Data @Entity @Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_id")
    private Long Id;

    @Column(name="payment_amount")
    private double amount;

    @Column(name="payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType type;

    @Column(name="payment_category")
    @Enumerated(EnumType.STRING)
    private PaymentCategory category;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
