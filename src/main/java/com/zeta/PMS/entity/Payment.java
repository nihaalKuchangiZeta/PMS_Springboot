package com.zeta.PMS.entity;

import com.zeta.PMS.enums.PaymentType;
import com.zeta.PMS.enums.PaymentCategory;
import com.zeta.PMS.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "payment_amount", nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_category", nullable = false)
    private PaymentCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status;

    @Column(name = "payment_date", nullable = false)
    private LocalDate date;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;
}
