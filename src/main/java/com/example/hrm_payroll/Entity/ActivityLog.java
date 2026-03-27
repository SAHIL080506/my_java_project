package com.example.hrm_payroll.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity_log")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = true)
    private ActivityLogRole role;

    // nullable — HR-level actions have no specific employee
    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = true)
    private Employee employee;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ActivityLog() {}

    // HR action (no employee)
    public ActivityLog(String actionType, String description, ActivityLogRole role) {
        this.actionType = actionType;
        this.description = description;
        this.role = role;
        this.employee = null;
        this.createdAt = LocalDateTime.now();
    }

    // Employee-specific action
    public ActivityLog(String actionType, String description, ActivityLogRole role, Employee employee) {
        this.actionType = actionType;
        this.description = description;
        this.role = role;
        this.employee = employee;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ActivityLogRole getRole() { return role; }
    public void setRole(ActivityLogRole role) { this.role = role; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
