package com.example.hrm_payroll.Controllers;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hrm_payroll.DTO.AttendanceResponse;
import com.example.hrm_payroll.DTO.TodayAttendanceStats;
import com.example.hrm_payroll.Entity.Attendance;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Entity.Users;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.UserRepository;
import com.example.hrm_payroll.Services.AttendanceService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    @Autowired private AttendanceService attendanceService;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;

    // ── Helpers 

    /** Resolve the logged-in employee from session. Returns null if not found or not an EMPLOYEE. */
    private Employee resolveEmployee(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return null;
        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        return employeeRepository.findByUser(user).orElse(null);
    }

    
    // POST /api/attendance/check-in
    

    /**
     * Employee checks in for today.
     * Only EMPLOYEE role can call this.
     */
    @PostMapping("/check-in")
    public ResponseEntity<?> checkIn(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        RoleType role = (RoleType) session.getAttribute("role");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Err("Not logged in"));
        }
        if (role != RoleType.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("Only employees can check in"));
        }

        Employee employee = resolveEmployee(session);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Err("Employee record not found"));
        }

        try {
            Attendance att = attendanceService.checkIn(employee.getEmpId());
            return ResponseEntity.ok(AttendanceResponse.fromEntity(att));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Err(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Err(e.getMessage()));
        }
    }

     
    // POST /api/attendance/check-out
    
    /**
     * Employee checks out for today.
     * Only allowed if check_in_time exists for today.
     */
    @PostMapping("/check-out")
    public ResponseEntity<?> checkOut(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        RoleType role = (RoleType) session.getAttribute("role");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Err("Not logged in"));
        }
        if (role != RoleType.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("Only employees can check out"));
        }

        Employee employee = resolveEmployee(session);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Err("Employee record not found"));
        }

        try {
            Attendance att = attendanceService.checkOut(employee.getEmpId());
            return ResponseEntity.ok(AttendanceResponse.fromEntity(att));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Err(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Err(e.getMessage()));
        }
    }

    
    // GET /api/attendance/status/{empId}
    
    /**
     * Get today's attendance status for a specific employee by empId.
     * Returns: { state: "NOT_CHECKED_IN" | "CHECKED_IN" | "CHECKED_OUT", checkInTime, checkOutTime }
     */
    @GetMapping("/status/{empId}")
    public ResponseEntity<?> getStatusByEmpId(@PathVariable Long empId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Err("Not logged in"));
        }

        Map<String, Object> detail = attendanceService.getTodayAttendanceDetail(empId);
        detail.put("empId", empId);
        detail.put("date", LocalDate.now().toString());
        return ResponseEntity.ok(detail);
    }

    
    // GET /api/attendance/today-status  (employee's own status)
    
    /**
     * Employee: get their own today's attendance state.
     * Returns: { state, empId, date, checkInTime?, checkOutTime? }
     */
    @GetMapping("/today-status")
    public ResponseEntity<?> getTodayStatus(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Employee employee = employeeRepository.findByUser(user).orElse(null);
        if (employee == null) {
            // HR has no attendance record
            return ResponseEntity.ok(Map.of("state", "N/A", "role", "HR"));
        }

        Map<String, Object> detail = attendanceService.getTodayAttendanceDetail(employee.getEmpId());
        detail.put("empId", employee.getEmpId());
        detail.put("date", LocalDate.now().toString());
        return ResponseEntity.ok(detail);
    }

    
    // GET /api/attendance/today-all

    /**
     * HR only: map of empId → display status for all employees today.
     * Status values: "ACTIVE" | "OFFLINE" | "INACTIVE"
     * Used by employees.html to render green/red dots.
     */
    @GetMapping("/today-all")
    public ResponseEntity<?> getAllTodayStatus(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("HR access required"));
        }
        return ResponseEntity.ok(attendanceService.getTodayStatusForAll());
    }

    
    // GET /api/attendance/today-stats
    
    /**
     * HR only: active/inactive counts for today's dashboard.
     */
    @GetMapping("/today-stats")
    public ResponseEntity<?> getTodayStats(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("HR access required"));
        }

        long total = employeeRepository.count();
        long active = attendanceService.countActiveToday();
        long inactive = attendanceService.countInactiveToday(total);

        return ResponseEntity.ok(new TodayAttendanceStats(active, inactive, total,
                LocalDate.now().toString()));
    }

    // ─── Error wrapper ────────────────────────────────────────────────────
    static class Err {
        private String message;
        public Err(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String m) { this.message = m; }
    }
}
