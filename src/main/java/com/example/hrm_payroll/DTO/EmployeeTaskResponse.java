package com.example.hrm_payroll.DTO;

import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.EmployeeTask;

public class EmployeeTaskResponse {

    private Long id;
    private Long empId;
    private String employeeName;
    private Long taskId;
    private String taskTitle;
    private String taskDescription;
    private boolean isBonus;
    private String status;
    private String proofFilePath;
    private LocalDateTime assignedDate;

    public EmployeeTaskResponse() {}

    public static EmployeeTaskResponse fromEntity(EmployeeTask et) {
        EmployeeTaskResponse dto = new EmployeeTaskResponse();
        dto.setId(et.getId());
        dto.setEmpId(et.getEmployee().getEmpId());
        dto.setEmployeeName(et.getEmployee().getFullName());
        dto.setTaskId(et.getTask().getTaskId());
        dto.setTaskTitle(et.getTask().getTitle());
        dto.setTaskDescription(et.getTask().getDescription());
        dto.setBonus(et.getTask().isBonus());
        dto.setStatus(et.getStatus());
        dto.setProofFilePath(et.getProofFilePath());
        dto.setAssignedDate(et.getAssignedDate());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }

    public boolean isBonus() { return isBonus; }
    public void setBonus(boolean bonus) { isBonus = bonus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProofFilePath() { return proofFilePath; }
    public void setProofFilePath(String proofFilePath) { this.proofFilePath = proofFilePath; }

    public LocalDateTime getAssignedDate() { return assignedDate; }
    public void setAssignedDate(LocalDateTime assignedDate) { this.assignedDate = assignedDate; }
}
