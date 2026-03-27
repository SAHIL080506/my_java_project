package com.example.hrm_payroll.DTO;

import com.example.hrm_payroll.Entity.Gender;
import com.example.hrm_payroll.Entity.RoleType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AddEmployeeRequest {

    @JsonProperty("first_name")
    private String firstName;      // Required

    @JsonProperty("middle_name")
    private String middleName;     // Optional

    @JsonProperty("last_name")
    private String lastName;       // Optional

    private String email;           // Required
    private String phone;           // Optional
    private String department;      // Optional
    private String designation;     // Optional
    private Gender gender;          // Optional
    private String address;         // Optional
    private RoleType role;          // Optional — defaults to EMPLOYEE

    public AddEmployeeRequest() {}

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

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

    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
}
