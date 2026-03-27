package com.example.hrm_payroll.Controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hrm_payroll.DTO.AddEmployeeRequest;
import com.example.hrm_payroll.DTO.DashboardStatsResponse;
import com.example.hrm_payroll.DTO.EmployeeResponse;
import com.example.hrm_payroll.DTO.NewEmployeeResponse;
import com.example.hrm_payroll.DTO.UserResponse;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;
import com.example.hrm_payroll.Repository.UserRepository;
import com.example.hrm_payroll.Services.EmployeeService;
import com.example.hrm_payroll.Services.UserService;

@RestController
@RequestMapping("/api/hr")
public class HRController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmployeeService employeeService;

    //  DASHBOARD 

    /**
     * GET /api/hr/dashboard/stats
     * Returns dashboard statistics: total employees, attendance rate, total payroll
     */
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse stats = employeeService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    //  EMPLOYEES 

    /**
     * GET /api/hr/employees
     * Returns all employees, with optional search filters
     * Query params: name, department, designation (all optional)
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation) {
        
        List<EmployeeResponse> employees = employeeService.searchEmployees(name, department, designation);
        return ResponseEntity.ok(employees);
    }

    /**
     * POST /api/hr/employees
     * Add a new employee (HR only - no user credentials created)
     */
    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody AddEmployeeRequest request) {
        EmployeeResponse employee = employeeService.addEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    /**
     * GET /api/hr/departments
     * Returns list of all distinct departments for filter dropdown
     */
    @GetMapping("/departments")
    public ResponseEntity<List<String>> getDepartments() {
        return ResponseEntity.ok(employeeService.getAllDepartments());
    }

    /**
     * GET /api/hr/designations
     * Returns list of all distinct designations for filter dropdown
     */
    @GetMapping("/designations")
    public ResponseEntity<List<String>> getDesignations() {
        return ResponseEntity.ok(employeeService.getAllDesignations());
    }

    //  USER APPROVALS 

    @GetMapping("/pending-users")
    public ResponseEntity<List<UserResponse>> getPendingUsers() {
        List<Users> pendingUsers = userRepository.findByStatus(UserStatus.PENDING);
        List<UserResponse> response = pendingUsers.stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{id}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable Long id) {
        Users updatedUser = userService.updateUserStatus(id, UserStatus.ACTIVE);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    @PutMapping("/users/{id}/reject")
    public ResponseEntity<UserResponse> rejectUser(@PathVariable Long id) {
        Users updatedUser = userService.updateUserStatus(id, UserStatus.REJECTED);
        return ResponseEntity.ok(UserResponse.fromEntity(updatedUser));
    }

    /**
     * GET /api/hr/employees/new-this-month
     * Returns employees who joined in the current calendar month
     */
    @GetMapping("/employees/new-this-month")
    public ResponseEntity<List<NewEmployeeResponse>> getNewEmployeesThisMonth() {
        return ResponseEntity.ok(employeeService.getNewEmployeesThisMonth());
    }
}
