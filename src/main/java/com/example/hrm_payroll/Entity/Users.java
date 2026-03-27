package com.example.hrm_payroll.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class Users {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @Column(name="username",unique = true,nullable = false)
    private String username;

     @Column(name = "email", unique = true, nullable = true, length = 100)
    private String email;

    @Column(name="password",nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role",nullable = false)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(name="status",nullable = false)
    private UserStatus status = UserStatus.PENDING;

    @Column(name="created_at",nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Users(){}

    public Users(Long userId, String username, String password, RoleType role, UserStatus status,
            LocalDateTime createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }


    public Long getUserId() {return userId;}
    public void setUserId(Long userId) {this.userId = userId;}

    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

     public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() {return password;}
    public void setPassword(String password) {this.password = password;}

    public RoleType getRole() {return role;}
    public void setRole(RoleType role) {this.role = role;}

    public UserStatus getStatus() {return status;}
    public void setStatus(UserStatus status) {this.status = status;}

    public LocalDateTime getCreatedAt() {return createdAt;}
    public void setCreatedAt(LocalDateTime createdAt) {this.createdAt = createdAt;}
}
