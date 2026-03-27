package com.example.hrm_payroll.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.hrm_payroll.DTO.EmployeeFullProfileResponse;
import com.example.hrm_payroll.DTO.ProfileResponse;
import com.example.hrm_payroll.DTO.ProfileUpdateRequest;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.EmployeeTask;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Repository.EmployeeTaskRepository;
import com.example.hrm_payroll.Services.ProfileService;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired private ProfileService profileService;
    @Autowired private EmployeeTaskRepository employeeTaskRepository;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    /**
     * GET /api/profile  OR  GET /api/profile/me
     * Returns the profile of the currently logged-in user (HR or Employee).
     * For HR users without an Employee record, returns a minimal profile from session.
     */
    @GetMapping({"", "/me"})
    public ResponseEntity<?> getMyProfile(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        RoleType role = (RoleType) session.getAttribute("role");

        try {
            Employee employee = profileService.getProfileByUserId(userId);
            int pct = profileService.getCompletionPercentage(employee);
            return ResponseEntity.ok(
                ProfileResponse.fromEntity(employee, pct, profileService.getMissingFields(employee))
            );
        } catch (RuntimeException e) {
            // HR user may not have an Employee record — return minimal HR profile
            if (role == RoleType.HR) {
                return ResponseEntity.ok(profileService.getHRProfileByUserId(userId));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrMsg(e.getMessage()));
        }
    }

    /**
     * PUT /api/profile  OR  PUT /api/profile/me
     */
    @PutMapping({"", "/me"})
    public ResponseEntity<?> updateMyProfile(
            @RequestBody ProfileUpdateRequest request,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (request.getFirstName() != null && request.getFirstName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrMsg("First name cannot be empty"));
        }
        if (request.getLastName() != null && request.getLastName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrMsg("Last name cannot be empty"));
        }

        Employee updated = profileService.updateProfile(userId, request);
        int pct = profileService.getCompletionPercentage(updated);
        return ResponseEntity.ok(
            ProfileResponse.fromEntity(updated, pct, profileService.getMissingFields(updated))
        );
    }

    /**
     * POST /api/profile/resume
     */
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            HttpSession session) throws IOException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Employee updated = profileService.uploadResume(userId, file);
        int pct = profileService.getCompletionPercentage(updated);
        return ResponseEntity.ok(
            ProfileResponse.fromEntity(updated, pct, profileService.getMissingFields(updated))
        );
    }

    /**
     * GET /api/profile/resume/download
     * Supports optional ?empId= param so HR can download any employee's resume.
     */
    @GetMapping("/resume/download")
    public ResponseEntity<Resource> downloadMyResume(
            @RequestParam(required = false) Long empId,
            HttpSession session) throws MalformedURLException {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Employee employee;
        if (empId != null) {
            // HR downloading specific employee's resume
            RoleType role = (RoleType) session.getAttribute("role");
            if (role != RoleType.HR)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            try {
                employee = profileService.getProfileByEmpId(empId);
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().build();
            }
        } else {
            employee = profileService.getProfileByUserId(userId);
        }

        if (employee.getResumePath() == null)
            return ResponseEntity.notFound().build();

        Path filePath = Paths.get(employee.getResumePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable())
            return ResponseEntity.notFound().build();

        String nameForFile = employee.getFullName().replace(" ", "_");
        if (nameForFile.isEmpty()) nameForFile = "resume";

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"resume_" + nameForFile + ".pdf\"")
            .body(resource);
    }

    /**
     * GET /api/profile/{empId}
     * HR only — full employee profile + tasks
     */
    @GetMapping("/{empId}")
    public ResponseEntity<?> getEmployeeProfile(
            @PathVariable Long empId,
            HttpSession session) {
        RoleType role = (RoleType) session.getAttribute("role");
        if (role != RoleType.HR)
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrMsg("Access denied: HR role required"));

        try {
            Employee emp = profileService.getProfileByEmpId(empId);
            List<EmployeeTask> tasks = employeeTaskRepository.findByEmpId(empId);
            return ResponseEntity.ok(EmployeeFullProfileResponse.from(emp, tasks));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrMsg(e.getMessage()));
        }
    }

    /**
     * GET /api/profile/hr/{empId}  (legacy — kept for backward compat)
     */
    @GetMapping("/hr/{empId}")
    public ResponseEntity<?> getEmployeeProfileByHR(
            @PathVariable Long empId,
            HttpSession session) {
        return getEmployeeProfile(empId, session);
    }

    static class ErrMsg {
        private String message;
        public ErrMsg(String msg) { this.message = msg; }
        public String getMessage() { return message; }
    }
}
