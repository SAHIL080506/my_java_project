package com.example.hrm_payroll.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.Attendance;
import com.example.hrm_payroll.Entity.AttendanceStatus;

public class AttendanceResponse {

    private Long attendanceId;
    private Long empId;
    private String employeeName;
    private LocalDate date;
    private AttendanceStatus status;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public AttendanceResponse() {}

    public static AttendanceResponse fromEntity(Attendance a) {
        AttendanceResponse dto = new AttendanceResponse();
        dto.setAttendanceId(a.getAttendanceId());
        dto.setEmpId(a.getEmployee().getEmpId());
        dto.setEmployeeName(a.getEmployee().getFullName());
        dto.setDate(a.getDate());
        dto.setStatus(a.getStatus());
        dto.setCheckInTime(a.getCheckInTime());
        dto.setCheckOutTime(a.getCheckOutTime());
        return dto;
    }

    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public LocalDateTime getCheckInTime() { return checkInTime; }
    public void setCheckInTime(LocalDateTime checkInTime) { this.checkInTime = checkInTime; }

    public LocalDateTime getCheckOutTime() { return checkOutTime; }
    public void setCheckOutTime(LocalDateTime checkOutTime) { this.checkOutTime = checkOutTime; }
}
