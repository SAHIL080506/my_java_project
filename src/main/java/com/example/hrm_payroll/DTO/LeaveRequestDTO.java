package com.example.hrm_payroll.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.LeaveRequest;
import com.example.hrm_payroll.Entity.LeaveStatus;
import com.example.hrm_payroll.Entity.LeaveType;

public class LeaveRequestDTO {

    private Long leaveId;
    private Long empId;
    private String employeeName;
    private String employeeEmail;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private LeaveStatus status;
    private LocalDateTime appliedDate;

    public LeaveRequestDTO() {}

    public static LeaveRequestDTO fromEntity(LeaveRequest lr) {
        LeaveRequestDTO dto = new LeaveRequestDTO();
        dto.setLeaveId(lr.getLeaveId());
        dto.setEmpId(lr.getEmployee().getEmpId());
        dto.setEmployeeName(lr.getEmployee().getFullName());

        // email lives in Users — null-safe
        if (lr.getEmployee().getUser() != null) {
            String email = lr.getEmployee().getUser().getEmail();
            dto.setEmployeeEmail(email != null ? email : lr.getEmployee().getUser().getUsername());
        }

        dto.setLeaveType(lr.getLeaveType());
        dto.setStartDate(lr.getStartDate());
        dto.setEndDate(lr.getEndDate());
        dto.setReason(lr.getReason());
        dto.setStatus(lr.getStatus());
        dto.setAppliedDate(lr.getAppliedDate());
        return dto;
    }

    public Long getLeaveId() { return leaveId; }
    public void setLeaveId(Long leaveId) { this.leaveId = leaveId; }

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }

    public LocalDateTime getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDateTime appliedDate) { this.appliedDate = appliedDate; }
}
