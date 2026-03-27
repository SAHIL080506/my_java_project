package com.example.hrm_payroll.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.Gender;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ProfileResponse {

    @JsonProperty("emp_id")
    private Long empId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("middle_name")
    private String middleName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("full_name")
    private String fullName;

    private String email;
    private String username;
    private String phone;
    private String address;
    private Gender gender;

    @JsonProperty("blood_group")
    private String bloodGroup;

    private LocalDate dob;
    private String designation;
    private String department;

    @JsonProperty("joining_date")
    private LocalDateTime joiningDate;

    @JsonProperty("resume_path")
    private String resumePath;

    @JsonProperty("profile_completed")
    private boolean profileCompleted;

    @JsonProperty("completion_percentage")
    private int completionPercentage;

    @JsonProperty("missing_fields")
    private List<String> missingFields;

    @JsonProperty("user_status")
    private UserStatus userStatus;

    private RoleType role;

    public static ProfileResponse fromEntity(Employee emp, int completionPercentage, List<String> missingFields) {
        ProfileResponse dto = new ProfileResponse();
        Users user = emp.getUser();

        dto.setEmpId(emp.getEmpId());
        dto.setFirstName(emp.getFirstName());
        dto.setMiddleName(emp.getMiddleName());
        dto.setLastName(emp.getLastName());
        dto.setFullName(emp.getFullName());
        dto.setPhone(emp.getPhone());
        dto.setAddress(emp.getAddress());
        dto.setGender(emp.getGender());
        dto.setBloodGroup(emp.getBloodGroup());
        dto.setDob(emp.getDob());
        dto.setDesignation(emp.getDesignation());
        dto.setDepartment(emp.getDepartment());
        dto.setJoiningDate(emp.getJoiningDate());
        dto.setResumePath(emp.getResumePath());
        dto.setProfileCompleted(emp.isProfileCompleted());
        dto.setCompletionPercentage(completionPercentage);
        dto.setMissingFields(missingFields);

        if (user != null) {
            dto.setUserId(user.getUserId());
            dto.setEmail(user.getEmail() != null ? user.getEmail() : user.getUsername());
            dto.setUsername(user.getUsername());
            dto.setUserStatus(user.getStatus());
            dto.setRole(user.getRole());
        }

        return dto;
    }

    // Getters and Setters
    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDateTime joiningDate) { this.joiningDate = joiningDate; }

    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }

    public boolean isProfileCompleted() { return profileCompleted; }
    public void setProfileCompleted(boolean profileCompleted) { this.profileCompleted = profileCompleted; }

    public int getCompletionPercentage() { return completionPercentage; }
    public void setCompletionPercentage(int completionPercentage) { this.completionPercentage = completionPercentage; }

    public List<String> getMissingFields() { return missingFields; }
    public void setMissingFields(List<String> missingFields) { this.missingFields = missingFields; }

    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
}
