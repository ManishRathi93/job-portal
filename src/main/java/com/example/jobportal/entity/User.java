package com.example.jobportal.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    @Column(name = "full_name")
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

//    // Custom constructor without id and createdAt (for registration)
//    public User(String email, String password, String fullName, UserType userType) {
//        this.email = email;
//        this.password = password;
//        this.fullName = fullName;
//        this.userType = userType;
//        this.createdAt = LocalDateTime.now();
//    }
}
enum UserType {
    JOB_SEEKER,
    EMPLOYER
}