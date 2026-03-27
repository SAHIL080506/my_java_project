package com.example.hrm_payroll.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hrm_payroll.DTO.ActivityLogResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.UserRepository;
import com.example.hrm_payroll.Services.ActivityLogService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    @Autowired private ActivityLogService activityLogService;
    @Autowired private UserRepository userRepository;
    @Autowired private EmployeeRepository employeeRepository;

    /**
     * GET /api/activity/recent
     * HR only — returns last 4 HR-role activities
     */
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentActivities(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) return ResponseEntity.status(403).build();
        List<ActivityLogResponse> list = activityLogService.getRecentHRActivities(4);
        return ResponseEntity.ok(list);
    }

    /**
     * GET /api/activity/recent/employee/{empId}
     * Returns last 4 activities for that specific employee
     */
    @GetMapping("/recent/employee/{empId}")
    public ResponseEntity<?> getRecentForEmployee(
            Long empId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(activityLogService.getRecentActivitiesForEmployee(empId, 4));
    }

    /**
     * GET /api/activity/recent/me
     * Employee — returns their own last 4 activities (session-resolved)
     */
    @GetMapping("/recent/me")
    public ResponseEntity<?> getMyRecentActivities(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) return ResponseEntity.status(401).build();

        com.example.hrm_payroll.Entity.Users user = userRepository.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(401).build();

        Employee emp = employeeRepository.findByUser(user).orElse(null);
        if (emp == null) return ResponseEntity.ok(List.of());

        return ResponseEntity.ok(activityLogService.getRecentActivitiesForEmployee(emp.getEmpId(), 4));
    }
}
