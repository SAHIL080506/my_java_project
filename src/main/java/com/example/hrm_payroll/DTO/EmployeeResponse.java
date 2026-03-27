package com.example.hrm_payroll.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.Gender;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EmployeeResponse {

    @JsonProperty("emp_id")
    private Long empId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("name")
    private String fullName;

    private String email;
    private String username;
    private String phone;
    private String department;
    private String designation;
    private Gender gender;
    private String address;

    @JsonProperty("blood_group")
    private String bloodGroup;

    private LocalDate dob;

    @JsonProperty("joining_date")
    private LocalDateTime joiningDate;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_status")
    private UserStatus userStatus;

    @JsonProperty("payroll_generated_current_month")
    private boolean payrollGeneratedForCurrentMonth;

    @JsonProperty("profile_completed")
    private boolean profileCompleted;

    public EmployeeResponse() {}

    public static EmployeeResponse fromEntity(Employee employee) {
        EmployeeResponse dto = new EmployeeResponse();
        Users user = employee.getUser();

        dto.setEmpId(employee.getEmpId());
        dto.setFirstName(employee.getFirstName());
        dto.setMiddleName(employee.getMiddleName());
        dto.setLastName(employee.getLastName());

        String fullName = employee.getFullName();
        dto.setFullName(fullName != null && !fullName.trim().isEmpty() ? fullName : "N/A");

        if (user != null) {
            dto.setEmail(user.getEmail() != null ? user.getEmail() : user.getUsername());
            dto.setUsername(user.getUsername());
            dto.setUserId(user.getUserId());
            dto.setUserStatus(user.getStatus());
        } else {
            dto.setEmail(null);
            dto.setUsername(null);
            dto.setUserId(null);
            dto.setUserStatus(null);
        }

        dto.setPhone(employee.getPhone());
        dto.setDepartment(employee.getDepartment());
        dto.setDesignation(employee.getDesignation());
        dto.setGender(employee.getGender());
        dto.setAddress(employee.getAddress());
        dto.setBloodGroup(employee.getBloodGroup());
        dto.setDob(employee.getDob());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setProfileCompleted(employee.isProfileCompleted());

        return dto;
    }

    // Getters and Setters
    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public LocalDateTime getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDateTime joiningDate) { this.joiningDate = joiningDate; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }

    public boolean isPayrollGeneratedForCurrentMonth() { return payrollGeneratedForCurrentMonth; }
    public void setPayrollGeneratedForCurrentMonth(boolean payrollGeneratedForCurrentMonth) {
        this.payrollGeneratedForCurrentMonth = payrollGeneratedForCurrentMonth;
    }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }
}
