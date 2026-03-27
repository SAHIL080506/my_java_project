package com.example.hrm_payroll.DTO;

import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.Employee;

public class NewEmployeeResponse {

    private Long empId;
    private String fullName;
    private String email;
    private String designation;
    private String department;
    private LocalDateTime joiningDate;

    public NewEmployeeResponse() {}

    public static NewEmployeeResponse fromEntity(Employee emp) {
        NewEmployeeResponse dto = new NewEmployeeResponse();
        dto.setEmpId(emp.getEmpId());
        dto.setFullName(emp.getFullName());
        // email lives in Users entity — null-safe
        if (emp.getUser() != null) {
            dto.setEmail(emp.getUser().getEmail() != null
                    ? emp.getUser().getEmail()
                    : emp.getUser().getUsername());
        }
        dto.setDesignation(emp.getDesignation());
        dto.setDepartment(emp.getDepartment());
        dto.setJoiningDate(emp.getJoiningDate());
        return dto;
    }

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDateTime joiningDate) { this.joiningDate = joiningDate; }
}
