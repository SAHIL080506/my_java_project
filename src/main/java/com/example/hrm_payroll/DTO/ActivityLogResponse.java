package com.example.hrm_payroll.DTO;

import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.ActivityLog;
import com.example.hrm_payroll.Entity.ActivityLogRole;

public class ActivityLogResponse {

    private Long id;
    private String actionType;
    private String description;
    private ActivityLogRole role;
    private Long empId;
    private String employeeName;
    private LocalDateTime createdAt;

    public ActivityLogResponse() {}

    public static ActivityLogResponse fromEntity(ActivityLog log) {
        ActivityLogResponse dto = new ActivityLogResponse();
        dto.setId(log.getId());
        dto.setActionType(log.getActionType());
        dto.setDescription(log.getDescription());
        dto.setRole(log.getRole());
        dto.setCreatedAt(log.getCreatedAt());
        if (log.getEmployee() != null) {
            dto.setEmpId(log.getEmployee().getEmpId());
            dto.setEmployeeName(log.getEmployee().getFullName());
        }
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ActivityLogRole getRole() { return role; }
    public void setRole(ActivityLogRole role) { this.role = role; }

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
