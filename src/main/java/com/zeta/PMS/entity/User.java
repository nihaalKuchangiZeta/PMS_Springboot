package com.zeta.PMS.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data @Entity @Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(name="user_name")
    private String name;

    @Column(name="user_email")
    private String email;

    @Column(name="user_role")
    private String role;
}
