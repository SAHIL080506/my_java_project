package com.example.hrm_payroll.Services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hrm_payroll.Entity.Attendance;
import com.example.hrm_payroll.Entity.AttendanceStatus;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Repository.AttendanceRepository;
import com.example.hrm_payroll.Repository.EmployeeRepository;

@Service
public class AttendanceService {

    @Autowired private AttendanceRepository attendanceRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ActivityLogService activityLogService;

    // ═══════════════════════════════════════════════════════════════════════
    // CHECK-IN LOGIC
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Employee checks in for today.
     *
     * Rules:
     * - If no record exists for today → create new record, set check_in_time, status = ACTIVE
     * - If record exists but check_in_time is null → update check_in_time, status = ACTIVE
     * - If already checked in (check_in_time exists) → throw IllegalStateException
     */
    @Transactional
    public Attendance checkIn(Long empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));

        LocalDate today = LocalDate.now();
        Attendance record = attendanceRepository.findByEmpIdAndDate(empId, today).orElse(null);

        // Already checked in today
        if (record != null && record.getCheckInTime() != null) {
            throw new IllegalStateException("Already checked in today");
        }

        // Create or reuse existing record
        if (record == null) {
            record = new Attendance();
            record.setEmployee(employee);
            record.setDate(today);
        }

        record.setCheckInTime(LocalDateTime.now());
        record.setCheckOutTime(null);
        record.setStatus(AttendanceStatus.PRESENT);

        Attendance saved = attendanceRepository.save(record);

        // Log the activity
        activityLogService.logEmployee(
                "ATTENDANCE_CHECKIN",
                employee.getFullName() + " checked in at " +
                        LocalDateTime.now().toLocalTime().withSecond(0).withNano(0),
                employee
        );

        return saved;
    }

    
    @Transactional
    public Attendance checkOut(Long empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));

        LocalDate today = LocalDate.now();
        Attendance record = attendanceRepository.findByEmpIdAndDate(empId, today).orElse(null);

        // Must have checked in first
        if (record == null || record.getCheckInTime() == null) {
            throw new IllegalStateException("Cannot check out: you have not checked in today");
        }

        // Already checked out
        if (record.getCheckOutTime() != null) {
            throw new IllegalStateException("Already checked out today");
        }

        record.setCheckOutTime(LocalDateTime.now());
        record.setStatus(AttendanceStatus.OFFLINE);

        Attendance saved = attendanceRepository.save(record);

        // Log the activity
        activityLogService.logEmployee(
                "ATTENDANCE_CHECKOUT",
                employee.getFullName() + " checked out at " +
                        LocalDateTime.now().toLocalTime().withSecond(0).withNano(0),
                employee
        );

        return saved;
    }

    
    public String getTodayStatus(Long empId) {
        Attendance record = attendanceRepository
                .findByEmpIdAndDate(empId, LocalDate.now())
                .orElse(null);

        if (record == null || record.getCheckInTime() == null) {
            return "INACTIVE";
        }
        if (record.getCheckOutTime() == null) {
            return "ACTIVE";   // display string only — DB stores PRESENT
        }
        return "OFFLINE";
    }

   
    public Map<String, Object> getTodayAttendanceDetail(Long empId) {
        Attendance record = attendanceRepository
                .findByEmpIdAndDate(empId, LocalDate.now())
                .orElse(null);

        Map<String, Object> result = new HashMap<>();

        if (record == null || record.getCheckInTime() == null) {
            result.put("state", "NOT_CHECKED_IN");
            result.put("checkInTime", null);
            result.put("checkOutTime", null);
        } else if (record.getCheckOutTime() == null) {
            result.put("state", "CHECKED_IN");
            result.put("checkInTime", record.getCheckInTime().toString());
            result.put("checkOutTime", null);
        } else {
            result.put("state", "CHECKED_OUT");
            result.put("checkInTime", record.getCheckInTime().toString());
            result.put("checkOutTime", record.getCheckOutTime().toString());
        }

        return result;
    }

   
    public long countActiveToday() {
        // PRESENT = checked in and not yet checked out (check_out_time IS NULL)
        return attendanceRepository.countActiveToday(LocalDate.now());
    }

    /**
     * HR Dashboard: INACTIVE count = total - active
     */
    public long countInactiveToday(long totalEmployees) {
        return totalEmployees - countActiveToday();
    }

    /**
     * Legacy: count PRESENT (now means ACTIVE) for backward compat with EmployeeService
     */
    public long countPresentToday() {
        return countActiveToday();
    }

    /**
     * Legacy: count absent for backward compat
     */
    public long countAbsentToday(long totalEmployees) {
        return countInactiveToday(totalEmployees);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // HR EMPLOYEE LIST — bulk status map
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * HR Employee List: map of empId → display status string.
     *
     * ACTIVE  = checked in, not yet checked out → 🟢
     * OFFLINE = checked out                      → 🔴
     * INACTIVE = no record / not checked in      → 🔴
     *
     * Used by employees.html to render green/red dots.
     */
    public Map<Long, String> getTodayStatusForAll() {
        Map<Long, String> result = new HashMap<>();
        attendanceRepository.findByDate(LocalDate.now()).forEach(a -> {
            String status;
            if (a.getCheckInTime() != null && a.getCheckOutTime() == null) {
                status = "ACTIVE";
            } else if (a.getCheckOutTime() != null) {
                status = "OFFLINE";
            } else {
                status = "INACTIVE";
            }
            result.put(a.getEmployee().getEmpId(), status);
        });
        return result;
    }
}