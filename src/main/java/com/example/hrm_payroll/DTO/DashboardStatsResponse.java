package com.example.hrm_payroll.DTO;

import java.math.BigDecimal;

public class DashboardStatsResponse {

    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long pendingApprovals;
    private double attendanceRate;
    private BigDecimal totalMonthlyPayroll;
    private long payrollPendingCount;
    private String currentMonth;
    // Attendance for today
    private long presentToday;
    private long absentToday;
    // Leave requests
    private long pendingLeaveCount;

    public DashboardStatsResponse() {}

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long v) { this.totalEmployees = v; }

    public long getActiveEmployees() { return activeEmployees; }
    public void setActiveEmployees(long v) { this.activeEmployees = v; }

    public long getInactiveEmployees() { return inactiveEmployees; }
    public void setInactiveEmployees(long v) { this.inactiveEmployees = v; }

    public long getPendingApprovals() { return pendingApprovals; }
    public void setPendingApprovals(long v) { this.pendingApprovals = v; }

    public double getAttendanceRate() { return attendanceRate; }
    public void setAttendanceRate(double v) { this.attendanceRate = v; }

    public BigDecimal getTotalMonthlyPayroll() { return totalMonthlyPayroll; }
    public void setTotalMonthlyPayroll(BigDecimal v) { this.totalMonthlyPayroll = v; }

    public long getPayrollPendingCount() { return payrollPendingCount; }
    public void setPayrollPendingCount(long v) { this.payrollPendingCount = v; }

    public String getCurrentMonth() { return currentMonth; }
    public void setCurrentMonth(String v) { this.currentMonth = v; }

    public long getPresentToday() { return presentToday; }
    public void setPresentToday(long v) { this.presentToday = v; }

    public long getAbsentToday() { return absentToday; }
    public void setAbsentToday(long v) { this.absentToday = v; }

    public long getPendingLeaveCount() { return pendingLeaveCount; }
    public void setPendingLeaveCount(long v) { this.pendingLeaveCount = v; }
}
