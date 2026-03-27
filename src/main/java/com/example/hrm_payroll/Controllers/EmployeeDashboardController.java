package com.example.hrm_payroll.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.hrm_payroll.DTO.EmployeeDashboardResponse;
import com.example.hrm_payroll.DTO.NewEmployeeResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.UserRepository;
import com.example.hrm_payroll.Services.EmployeeDashboardService;
import com.example.hrm_payroll.Services.EmployeeService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/employee")
public class EmployeeDashboardController {

    @Autowired private EmployeeDashboardService employeeDashboardService;
    @Autowired private EmployeeService employeeService;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRepository userRepository;

    /**
     * GET /api/employee/dashboard
     * Returns full dashboard data for the currently logged-in employee.
     * Resolves empId from session userId → employee record.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getEmployeeDashboard(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        RoleType role = (RoleType) session.getAttribute("role");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (role != RoleType.EMPLOYEE) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrMsg("Access denied: Employee role required"));
        }

        // Resolve employee from userId
        com.example.hrm_payroll.Entity.Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        Employee employee = employeeRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Employee profile not found for this user"));

        try {
            EmployeeDashboardResponse resp = employeeDashboardService.getDashboardForEmployee(employee.getEmpId());
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrMsg(e.getMessage()));
        }
    }

    /**
     * GET /api/employees/new-this-month
     * HR-only: returns employees who joined in the current calendar month
     */
    @GetMapping("/new-this-month")
    public ResponseEntity<?> getNewEmployeesThisMonth(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrMsg("Access denied: HR role required"));
        }
        List<NewEmployeeResponse> newEmps = employeeService.getNewEmployeesThisMonth();
        return ResponseEntity.ok(newEmps);
    }

    static class ErrMsg {
        private String message;
        public ErrMsg(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String m) { this.message = m; }
    }
}
