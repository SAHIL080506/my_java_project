package com.example.hrm_payroll.DTO;

public class TodayAttendanceStats {
    private long presentCount;
    private long absentCount;
    private long totalEmployees;
    private String date;

    public TodayAttendanceStats() {}

    public TodayAttendanceStats(long presentCount, long absentCount, long totalEmployees, String date) {
        this.presentCount = presentCount;
        this.absentCount = absentCount;
        this.totalEmployees = totalEmployees;
        this.date = date;
    }

    public long getPresentCount() { return presentCount; }
    public void setPresentCount(long presentCount) { this.presentCount = presentCount; }

    public long getAbsentCount() { return absentCount; }
    public void setAbsentCount(long absentCount) { this.absentCount = absentCount; }

    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
}
