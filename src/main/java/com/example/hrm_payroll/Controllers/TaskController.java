package com.example.hrm_payroll.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.hrm_payroll.DTO.EmployeeTaskResponse;
import com.example.hrm_payroll.DTO.OnboardingTaskResponse;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Services.TaskService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // HR-ONLY ENDPOINTS

    @PostMapping("/create")
    public ResponseEntity<?> createTask(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(defaultValue = "false") boolean isBonus,
            HttpSession session) {

        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMsg("Access denied: HR role required"));

        try {
            OnboardingTaskResponse resp = taskService.createTask(title, description, isBonus);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMsg(e.getMessage()));
        }
    }

    @GetMapping("/templates")
    public ResponseEntity<?> getAllTemplates(HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMsg("Access denied: HR role required"));
        return ResponseEntity.ok(taskService.getAllTaskTemplates());
    }

    @PostMapping("/assign")
    public ResponseEntity<?> assignTask(
            @RequestParam Long empId,
            @RequestParam Long taskId,
            HttpSession session) {

        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMsg("Access denied: HR role required"));

        try {
            EmployeeTaskResponse resp = taskService.assignTask(empId, taskId);
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMsg(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMsg(e.getMessage()));
        }
    }

    @PostMapping("/reassign")
    public ResponseEntity<?> reassignTask(
            @RequestParam Long empId,
            @RequestParam Long taskId,
            HttpSession session) {

        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMsg("Access denied: HR role required"));

        try {
            EmployeeTaskResponse resp = taskService.reassignTask(empId, taskId);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMsg(e.getMessage()));
        }
    }

    /**
     * PUT /api/tasks/{assignmentId}/done
     * HR marks a task as DONE/COMPLETED for an employee
     */
    @PutMapping("/{assignmentId}/done")
    public ResponseEntity<?> markTaskDone(
            @PathVariable Long assignmentId,
            HttpSession session) {

        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMsg("Access denied: HR role required"));

        try {
            EmployeeTaskResponse resp = taskService.markTaskDoneByHR(assignmentId);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMsg(e.getMessage()));
        }
    }

    // SHARED ENDPOINT

    @GetMapping("/employee/{empId}")
    public ResponseEntity<?> getTasksForEmployee(
            @PathVariable Long empId,
            HttpSession session) {

        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            List<EmployeeTaskResponse> tasks = taskService.getTasksForEmployee(empId);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMsg("Error fetching tasks"));
        }
    }

    // EMPLOYEE ENDPOINT

    @PostMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> completeTask(
            @RequestParam Long employeeTaskId,
            @RequestParam("proofFile") MultipartFile proofFile,
            HttpSession session) {

        Long sessionUserId = (Long) session.getAttribute("userId");
        if (sessionUserId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        try {
            EmployeeTaskResponse resp = taskService.completeTask(employeeTaskId, proofFile);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorMsg(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMsg(e.getMessage()));
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMsg("File upload failed: " + e.getMessage()));
        }
    }

    static class ErrorMsg {
        private String message;
        public ErrorMsg(String message) { this.message = message; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
