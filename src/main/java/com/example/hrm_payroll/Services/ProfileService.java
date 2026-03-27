package com.example.hrm_payroll.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import com.example.hrm_payroll.DTO.ProfileUpdateRequest;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.Users;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.UserRepository;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static java.util.Map.entry;
import java.util.UUID;

@Service
public class ProfileService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private UserRepository userRepository;

    @Value("${app.upload.dir:uploads/resumes}")
    private String uploadDir;

    // Fetch profile by userId (for logged-in user)
    public Employee getProfileByUserId(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return employeeRepository.findByUser(user)
            .orElseThrow(() -> new RuntimeException("Employee profile not found"));
    }

    /**
     * Returns a minimal profile map for HR users who have no Employee record.
     */
    public Map<String, Object> getHRProfileByUserId(Long userId) {
        Users user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return Map.ofEntries(
        entry("emp_id", -1L),
        entry("username", user.getUsername() != null ? user.getUsername() : ""),
        entry("email", user.getEmail() != null ? user.getEmail() : ""),
        entry("role", user.getRole() != null ? user.getRole().name() : "HR"),
        entry("first_name", ""),
        entry("middle_name", ""),
        entry("last_name", ""),
        entry("name", user.getUsername() != null ? user.getUsername() : "HR User"),
        entry("completion_percentage", 0),
        entry("missing_fields", List.of()),
        entry("profile_completed", false)
    );
    }

    // Fetch profile by empId (HR viewing any employee)
    public Employee getProfileByEmpId(Long empId) {
        return employeeRepository.findById(empId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));
    }

    // Update profile fields
    @Transactional
    public Employee updateProfile(Long userId, ProfileUpdateRequest request) {
        Employee employee = getProfileByUserId(userId);

        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            employee.setFirstName(request.getFirstName().trim());
        }
        if (request.getMiddleName() != null) {
            String mn = request.getMiddleName().trim();
            employee.setMiddleName(mn.isEmpty() ? null : mn);
        }
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            employee.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null)       employee.setPhone(request.getPhone().trim());
        if (request.getAddress() != null)     employee.setAddress(request.getAddress().trim());
        if (request.getBloodGroup() != null)  employee.setBloodGroup(request.getBloodGroup().trim());
        if (request.getDob() != null)         employee.setDob(request.getDob());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation().trim());
        if (request.getDepartment() != null)  employee.setDepartment(request.getDepartment().trim());
        if (request.getGender() != null)      employee.setGender(request.getGender());

        employee.setProfileCompleted(isProfileComplete(employee));
        return employeeRepository.save(employee);
    }

    // Resume upload
    @Transactional
    public Employee uploadResume(Long userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new RuntimeException("File is empty");

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.toLowerCase().endsWith(".pdf"))
            throw new RuntimeException("Only PDF files are allowed for resume");

        if (file.getSize() > 5 * 1024 * 1024)
            throw new RuntimeException("File size must be under 5MB");

        String safeFileName = "resume_" + userId + "_" + UUID.randomUUID() + ".pdf";
        Path uploadPath = Paths.get(uploadDir);
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(safeFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Employee employee = getProfileByUserId(userId);

        if (employee.getResumePath() != null) {
            try { Files.deleteIfExists(Paths.get(employee.getResumePath())); }
            catch (IOException ignored) {}
        }

        employee.setResumePath(filePath.toString());
        employee.setProfileCompleted(isProfileComplete(employee));
        return employeeRepository.save(employee);
    }

    public boolean isProfileComplete(Employee e) {
        return e.getFirstName()   != null && !e.getFirstName().isBlank()
            && e.getLastName()    != null && !e.getLastName().isBlank()
            && e.getUser()        != null
            && e.getUser().getEmail() != null && !e.getUser().getEmail().isBlank()
            && e.getPhone()       != null && !e.getPhone().isBlank()
            && e.getGender()      != null
            && e.getDob()         != null
            && e.getAddress()     != null && !e.getAddress().isBlank()
            && e.getDesignation() != null && !e.getDesignation().isBlank()
            && e.getDepartment()  != null && !e.getDepartment().isBlank()
            && e.getResumePath()  != null && !e.getResumePath().isBlank();
    }

    public int getCompletionPercentage(Employee e) {
        int total  = 10;
        int filled = 0;
        if (e.getFirstName()   != null && !e.getFirstName().isBlank())   filled++;
        if (e.getLastName()    != null && !e.getLastName().isBlank())    filled++;
        if (e.getPhone()       != null && !e.getPhone().isBlank())       filled++;
        if (e.getGender()      != null)                                   filled++;
        if (e.getDob()         != null)                                   filled++;
        if (e.getAddress()     != null && !e.getAddress().isBlank())     filled++;
        if (e.getDesignation() != null && !e.getDesignation().isBlank()) filled++;
        if (e.getDepartment()  != null && !e.getDepartment().isBlank())  filled++;
        if (e.getResumePath()  != null && !e.getResumePath().isBlank())  filled++;
        if (e.getUser() != null
                && e.getUser().getEmail() != null
                && !e.getUser().getEmail().isBlank())                    filled++;
        return (int) Math.round((double) filled / total * 100);
    }

    public List<String> getMissingFields(Employee e) {
        List<String> missing = new ArrayList<>();
        if (e.getLastName()    == null || e.getLastName().isBlank())    missing.add("last name");
        if (e.getPhone()       == null || e.getPhone().isBlank())       missing.add("phone");
        if (e.getGender()      == null)                                  missing.add("gender");
        if (e.getDob()         == null)                                  missing.add("date of birth");
        if (e.getAddress()     == null || e.getAddress().isBlank())     missing.add("address");
        if (e.getDesignation() == null || e.getDesignation().isBlank()) missing.add("designation");
        if (e.getDepartment()  == null || e.getDepartment().isBlank())  missing.add("department");
        if (e.getResumePath()  == null || e.getResumePath().isBlank())  missing.add("resume");
        return missing;
    }
}
