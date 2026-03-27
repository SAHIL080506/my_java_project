package com.example.hrm_payroll.DTO;

import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.EmployeeTask;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeFullProfileResponse {

    @JsonProperty("emp_id")
    private Long empId;

    @JsonProperty("full_name")
    private String fullName;

    private String username;
    private String email;
    private String phone;
    private String department;
    private String designation;
    private String address;

    @JsonProperty("blood_group")
    private String bloodGroup;

    private LocalDate dob;
    private String gender;

    @JsonProperty("joining_date")
    private LocalDateTime joiningDate;

    @JsonProperty("resume_path")
    private String resumePath;

    @JsonProperty("profile_completed")
    private boolean profileCompleted;

    @JsonProperty("user_status")
    private String userStatus;

    private List<TaskSummary> tasks;

    @JsonProperty("total_assigned")
    private int totalAssigned;

    @JsonProperty("total_completed")
    private int totalCompleted;

    public static EmployeeFullProfileResponse from(Employee emp, List<EmployeeTask> taskList) {
        EmployeeFullProfileResponse r = new EmployeeFullProfileResponse();
        r.empId            = emp.getEmpId();
        r.fullName         = emp.getFullName();
        r.username         = (emp.getUser() != null) ? emp.getUser().getUsername() : null;
        r.email            = (emp.getUser() != null && emp.getUser().getEmail() != null)
                             ? emp.getUser().getEmail()
                             : (emp.getUser() != null ? emp.getUser().getUsername() : null);
        r.phone            = emp.getPhone();
        r.department       = emp.getDepartment();
        r.designation      = emp.getDesignation();
        r.address          = emp.getAddress();
        r.bloodGroup       = emp.getBloodGroup();
        r.dob              = emp.getDob();
        r.gender           = emp.getGender() != null ? emp.getGender().name() : null;
        r.joiningDate      = emp.getJoiningDate();
        r.resumePath       = emp.getResumePath();
        r.profileCompleted = emp.isProfileCompleted();
        r.userStatus       = emp.getUserStatus() != null ? emp.getUserStatus().name() : null;
        r.tasks            = taskList.stream().map(TaskSummary::from).collect(Collectors.toList());
        r.totalAssigned    = taskList.size();
        r.totalCompleted   = (int) taskList.stream()
                                .filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()))
                                .count();
        return r;
    }

    // Getters
    public Long getEmpId()            { return empId; }
    public String getFullName()       { return fullName; }
    public String getUsername()       { return username; }
    public String getEmail()          { return email; }
    public String getPhone()          { return phone; }
    public String getDepartment()     { return department; }
    public String getDesignation()    { return designation; }
    public String getAddress()        { return address; }
    public String getBloodGroup()     { return bloodGroup; }
    public LocalDate getDob()         { return dob; }
    public String getGender()         { return gender; }
    public LocalDateTime getJoiningDate() { return joiningDate; }
    public String getResumePath()     { return resumePath; }
    public boolean isProfileCompleted() { return profileCompleted; }
    public String getUserStatus()     { return userStatus; }
    public List<TaskSummary> getTasks() { return tasks; }
    public int getTotalAssigned()     { return totalAssigned; }
    public int getTotalCompleted()    { return totalCompleted; }

    // ── Inner DTO for tasks ──
    public static class TaskSummary {
        @JsonProperty("task_id")
        private Long taskId;
        private String title;
        private String description;
        private String status;

        @JsonProperty("assignment_id")
        private Long assignmentId;

        @JsonProperty("is_bonus")
        private boolean isBonus;

        @JsonProperty("assigned_date")
        private LocalDateTime assignedDate;

        @JsonProperty("proof_file_path")
        private String proofFilePath;

        public static TaskSummary from(EmployeeTask et) {
            TaskSummary t = new TaskSummary();
            t.assignmentId  = et.getId();
            t.taskId        = et.getTask().getTaskId();
            t.title         = et.getTask().getTitle();
            t.description   = et.getTask().getDescription();
            t.status        = et.getStatus();
            t.isBonus       = et.getTask().isBonus();
            t.assignedDate  = et.getAssignedDate();
            t.proofFilePath = et.getProofFilePath();
            return t;
        }

        public Long getTaskId()         { return taskId; }
        public Long getAssignmentId()   { return assignmentId; }
        public String getTitle()        { return title; }
        public String getDescription()  { return description; }
        public String getStatus()       { return status; }
        public boolean isBonus()        { return isBonus; }
        public LocalDateTime getAssignedDate() { return assignedDate; }
        public String getProofFilePath() { return proofFilePath; }
    }
}
