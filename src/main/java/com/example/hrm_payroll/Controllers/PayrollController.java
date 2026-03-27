package com.example.hrm_payroll.Controllers;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hrm_payroll.DTO.GeneratePayrollRequest;
import com.example.hrm_payroll.DTO.PayrollResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.PayrollRepository;
import com.example.hrm_payroll.Repository.UserRepository;
import com.example.hrm_payroll.Services.ActivityLogService;
import com.example.hrm_payroll.Services.PayrollService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    @Autowired private PayrollService payrollService;
    @Autowired private ActivityLogService activityLogService;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private UserRepository userRepository;

    /** POST /api/payroll/generate — HR only */
    @PostMapping("/generate")
    public ResponseEntity<?> generatePayroll(
            @RequestBody GeneratePayrollRequest request,
            HttpSession session) {

        RoleType role = (RoleType) session.getAttribute("role");
        if (role == null || role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied: HR role required"));
        }
        try {
            PayrollResponse response = payrollService.generatePayroll(request);
            Employee emp = employeeRepository.findById(request.getEmpId()).orElse(null);
            String desc = "Payroll generated for " +
                    (emp != null ? emp.getFullName() : "Employee #" + request.getEmpId()) +
                    " — Month: " + request.getSalaryMonth();
            activityLogService.logHR("PAYROLL_GENERATED", desc);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred while generating payroll"));
        }
    }

    /** GET /api/payroll/exists?empId=1&month=2026-02 — HR only */
    @GetMapping("/exists")
    public ResponseEntity<?> checkPayrollExists(
            @RequestParam Long empId,
            @RequestParam String month,
            HttpSession session) {

        RoleType role = (RoleType) session.getAttribute("role");
        if (role == null || role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied: HR role required"));
        }
        try {
            boolean exists = payrollService.payrollExists(empId, month);
            return ResponseEntity.ok(new PayrollExistsResponse(exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error checking payroll status"));
        }
    }

    /**
     * GET /api/payroll/payslip?empId=1&month=2026-02
     * HR: any employee. EMPLOYEE: only their own.
     */
    @GetMapping("/payslip")
    public ResponseEntity<?> generatePayslip(
            @RequestParam Long empId,
            @RequestParam String month,
            HttpSession session) {

        Long sessionUserId = (Long) session.getAttribute("userId");
        RoleType role = (RoleType) session.getAttribute("role");
        if (sessionUserId == null || role == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (role == RoleType.EMPLOYEE) {
            com.example.hrm_payroll.Entity.Users user = userRepository.findById(sessionUserId).orElse(null);
            if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            Employee emp = employeeRepository.findByUser(user).orElse(null);
            if (emp == null || !emp.getEmpId().equals(empId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Access denied: You can only download your own payslip".getBytes());
            }
        }

        try {
            byte[] pdfBytes = payrollService.generatePayslipPDF(empId, month);
            Employee emp = employeeRepository.findById(empId).orElse(null);
            String desc = (emp != null ? emp.getFullName() : "Employee #" + empId)
                    + " downloaded payslip for " + month;
            activityLogService.logEmployee("PAYSLIP_DOWNLOADED", desc, emp);

            String filename = "payslip_emp" + empId + "_" + month + ".pdf";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(("Error: " + e.getMessage()).getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while generating payslip".getBytes());
        }
    }

    /** GET /api/payroll/pending-count — HR only */
    @GetMapping("/pending-count")
    public ResponseEntity<?> getPayrollPendingCount(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Access denied: HR role required"));
        }
        String monthStr = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        long totalEmployees = employeeRepository.count();
        List<Long> paidEmpIds = payrollRepository.findEmpIdsWithPayrollForMonth(monthStr);
        long pendingCount = Math.max(0, totalEmployees - paidEmpIds.size());
        return ResponseEntity.ok(new PendingCountResponse(pendingCount, monthStr));
    }

    static class ErrorResponse {
        private String message;
        public ErrorResponse(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String m) { this.message = m; }
    }

    static class PayrollExistsResponse {
        private boolean exists;
        public PayrollExistsResponse(boolean exists) { this.exists = exists; }
        public boolean isExists() { return exists; }
        public void setExists(boolean e) { this.exists = e; }
    }

    static class PendingCountResponse {
        private long pendingCount;
        private String month;
        public PendingCountResponse(long p, String m) { this.pendingCount = p; this.month = m; }
        public long getPendingCount() { return pendingCount; }
        public void setPendingCount(long p) { this.pendingCount = p; }
        public String getMonth() { return month; }
        public void setMonth(String m) { this.month = m; }
    }
}