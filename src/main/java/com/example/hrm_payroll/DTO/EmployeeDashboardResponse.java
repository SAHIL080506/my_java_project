package com.example.hrm_payroll.DTO;

import java.math.BigDecimal;
import java.util.List;

public class EmployeeDashboardResponse {

    private Long empId;
    private String employeeName;
    private String designation;
    private String department;

    // Salary card
    private BigDecimal currentMonthSalary;   // null if not yet generated
    private boolean payrollGenerated;
    private String currentMonth;

    // Task stats
    private long totalTasksAssigned;
    private long completedTasks;
    private long pendingTasks;
    private long completedBonusTasks;
    private BigDecimal bonusAmount; // ₹500 per completed bonus task 

    // Tasks list
    private List<EmployeeTaskResponse> tasks;

    public EmployeeDashboardResponse() {}

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getCurrentMonthSalary() { return currentMonthSalary; }
    public void setCurrentMonthSalary(BigDecimal currentMonthSalary) { this.currentMonthSalary = currentMonthSalary; }

    public boolean isPayrollGenerated() { return payrollGenerated; }
    public void setPayrollGenerated(boolean payrollGenerated) { this.payrollGenerated = payrollGenerated; }

    public String getCurrentMonth() { return currentMonth; }
    public void setCurrentMonth(String currentMonth) { this.currentMonth = currentMonth; }

    public long getTotalTasksAssigned() { return totalTasksAssigned; }
    public void setTotalTasksAssigned(long totalTasksAssigned) { this.totalTasksAssigned = totalTasksAssigned; }

    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }

    public long getPendingTasks() { return pendingTasks; }
    public void setPendingTasks(long pendingTasks) { this.pendingTasks = pendingTasks; }

    public long getCompletedBonusTasks() { return completedBonusTasks; }
    public void setCompletedBonusTasks(long completedBonusTasks) { this.completedBonusTasks = completedBonusTasks; }

    public BigDecimal getBonusAmount() { return bonusAmount; }
    public void setBonusAmount(BigDecimal bonusAmount) { this.bonusAmount = bonusAmount; }

    public List<EmployeeTaskResponse> getTasks() { return tasks; }
    public void setTasks(List<EmployeeTaskResponse> tasks) { this.tasks = tasks; }
}
