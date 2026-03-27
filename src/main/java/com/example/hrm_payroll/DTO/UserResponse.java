package com.example.hrm_payroll.DTO;

import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;

public class UserResponse {

    private Long userId;
    private String username;
    private String email;
    private RoleType role;
    private UserStatus status;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public static UserResponse fromEntity(Users user) {
        UserResponse r = new UserResponse();
        r.setUserId(user.getUserId());
        r.setUsername(user.getUsername());
        r.setEmail(user.getEmail());
        r.setRole(user.getRole());
        r.setStatus(user.getStatus());
        r.setCreatedAt(user.getCreatedAt());
        return r;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
