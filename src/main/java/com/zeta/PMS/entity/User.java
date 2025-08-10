package com.zeta.PMS.entity;

import com.zeta.PMS.enums.UserRole;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="user_name", nullable = false)
    private String name;

    @Column(name="user_email", unique = true, nullable = false)
    private String email;

    @Column(name="user_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
