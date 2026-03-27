package com.example.hrm_payroll.Controllers;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hrm_payroll.DTO.LeaveRequestDTO;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.UserRepository;
import com.example.hrm_payroll.Services.LeaveService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired private LeaveService leaveService;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;

    /**
     * POST /api/leaves/apply
     * Employee applies for leave. empId resolved from session.
     * Body: { leaveType, startDate, endDate, reason }
     */
    @PostMapping("/apply")
    public ResponseEntity<?> applyLeave(
            @RequestBody Map<String, String> body,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        RoleType role = (RoleType) session.getAttribute("role");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (role != RoleType.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new Err("Only employees can apply for leave"));
        }

        com.example.hrm_payroll.Entity.Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Employee profile not found"));

        try {
            String leaveType = body.get("leaveType");
            LocalDate startDate = LocalDate.parse(body.get("startDate"));
            LocalDate endDate = LocalDate.parse(body.get("endDate"));
            String reason = body.getOrDefault("reason", "");

            LeaveRequestDTO dto = leaveService.applyLeave(
                    employee.getEmpId(), leaveType, startDate, endDate, reason);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Err(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Err("Failed to apply leave: " + e.getMessage()));
        }
    }

    /**
     * GET /api/leaves/employee/{empId}
     * Get leave history for a specific employee.
     * Employee: can only see their own. HR: can see any.
     */
    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getEmployeeLeaves(
            @PathVariable Long empId,
            HttpSession session) {

        Long userId = (Long) session.getAttribute("userId");
        RoleType role = (RoleType) session.getAttribute("role");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        // Employee can only view their own
        if (role == RoleType.EMPLOYEE) {
            com.example.hrm_payroll.Entity.Users user = userRepository.findById(userId).orElse(null);
            Employee employee = user != null ? employeeRepository.findByUser(user).orElse(null) : null;
            if (employee == null || !employee.getEmpId().equals(empId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new Err("Access denied"));
            }
        }

        return ResponseEntity.ok(leaveService.getLeavesByEmployee(empId));
    }

    /**
     * GET /api/leaves/my
     * Employee: get own leave history (session-resolved empId)
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyLeaves(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        com.example.hrm_payroll.Entity.Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Employee employee = employeeRepository.findByUser(user).orElse(null);
        if (employee == null) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(leaveService.getLeavesByEmployee(employee.getEmpId()));
    }

    /**
     * GET /api/leaves/all — HR only
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllLeaves(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("HR access required"));
        }
        return ResponseEntity.ok(leaveService.getAllLeaves());
    }

    /**
     * GET /api/leaves/pending-count — HR only
     */
    @GetMapping("/pending-count")
    public ResponseEntity<?> getPendingCount(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("HR access required"));
        }
        return ResponseEntity.ok(Map.of("pendingCount", leaveService.countPendingLeaves()));
    }

    /**
     * PUT /api/leaves/{id}/approve — HR only
     */
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeave(@PathVariable Long id, HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("HR access required"));
        }
        try {
            return ResponseEntity.ok(leaveService.approveLeave(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Err(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Err(e.getMessage()));
        }
    }

    /**
     * PUT /api/leaves/{id}/reject — HR only
     */
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeave(@PathVariable Long id, HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Err("HR access required"));
        }
        try {
            return ResponseEntity.ok(leaveService.rejectLeave(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new Err(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new Err(e.getMessage()));
        }
    }

    static class Err {
        private String message;
        public Err(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String m) { this.message = m; }
    }
}
