package com.example.hrm_payroll.Services;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.hrm_payroll.DTO.EmployeeTaskResponse;
import com.example.hrm_payroll.DTO.OnboardingTaskResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.EmployeeTask;
import com.example.hrm_payroll.Entity.OnboardingTask;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.EmployeeTaskRepository;
import com.example.hrm_payroll.Repository.OnboardingTaskRepository;

@Service
public class TaskService {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Autowired private OnboardingTaskRepository onboardingTaskRepository;
    @Autowired private EmployeeTaskRepository employeeTaskRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ActivityLogService activityLogService;

    // ===================== ONBOARDING TASK CRUD =====================

    /**
     * HR creates a new onboarding task template
     */
    @Transactional
    public OnboardingTaskResponse createTask(String title, String description, boolean isBonus) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Task title is required");
        }
        OnboardingTask task = new OnboardingTask();
        task.setTitle(title.trim());
        task.setDescription(description != null ? description.trim() : null);
        task.setBonus(isBonus);
        OnboardingTask saved = onboardingTaskRepository.save(task);
        activityLogService.log("TASK_CREATED", "HR created task: " + saved.getTitle());
        return OnboardingTaskResponse.fromEntity(saved);
    }

    /**
     * Get all task templates (for HR dropdown when assigning)
     */
    public List<OnboardingTaskResponse> getAllTaskTemplates() {
        return onboardingTaskRepository.findAll()
                .stream()
                .map(OnboardingTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    

    /**
     * HR assigns a task to an employee.
     * If already assigned, throws — use reassign instead.
     */
    @Transactional
    public EmployeeTaskResponse assignTask(Long empId, Long taskId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));
        OnboardingTask task = onboardingTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        // Prevent duplicate assignment
        if (employeeTaskRepository.findByEmpIdAndTaskId(empId, taskId).isPresent()) {
            throw new IllegalStateException("Task already assigned to this employee. Use reassign instead.");
        }

        EmployeeTask et = new EmployeeTask();
        et.setEmployee(employee);
        et.setTask(task);
        et.setStatus("PENDING");
        et.setAssignedDate(LocalDateTime.now());

        EmployeeTask saved = employeeTaskRepository.save(et);

        activityLogService.log("TASK_ASSIGNED",
                "Task '" + task.getTitle() + "' assigned to " + employee.getFullName(), employee);

        return EmployeeTaskResponse.fromEntity(saved);
    }

    /**
     * HR reassigns (resets) an already-assigned task back to PENDING.
     * Clears proof file path and status.
     */
    @Transactional
    public EmployeeTaskResponse reassignTask(Long empId, Long taskId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));
        OnboardingTask task = onboardingTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        EmployeeTask et = employeeTaskRepository.findByEmpIdAndTaskId(empId, taskId)
                .orElseGet(() -> {
                    // If not previously assigned, just assign fresh
                    EmployeeTask fresh = new EmployeeTask();
                    fresh.setEmployee(employee);
                    fresh.setTask(task);
                    return fresh;
                });

        et.setStatus("PENDING");
        et.setProofFilePath(null);
        et.setAssignedDate(LocalDateTime.now());

        EmployeeTask saved = employeeTaskRepository.save(et);

        activityLogService.log("TASK_REASSIGNED",
                "Task '" + task.getTitle() + "' reassigned to " + employee.getFullName(), employee);

        return EmployeeTaskResponse.fromEntity(saved);
    }

    

    /**
     * Employee marks a task complete and uploads proof file.
     * File is mandatory.
     */
    @Transactional
    public EmployeeTaskResponse completeTask(Long employeeTaskId, MultipartFile proofFile) throws IOException {

        EmployeeTask et = employeeTaskRepository.findById(employeeTaskId)
                .orElseThrow(() -> new IllegalArgumentException("Assigned task not found: " + employeeTaskId));

        if ("COMPLETED".equals(et.getStatus())) {
            throw new IllegalStateException("Task is already marked as completed");
        }

        // File is mandatory
        if (proofFile == null || proofFile.isEmpty()) {
            throw new IllegalArgumentException("Proof file is required to complete a task");
        }

        // Validate file type (allow images and PDF only)
        String originalFilename = proofFile.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Invalid file");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        if (!List.of("jpg", "jpeg", "png", "pdf").contains(ext)) {
            throw new IllegalArgumentException("Only JPG, PNG, or PDF files are allowed as proof");
        }

        // Save file to upload directory
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String uniqueFilename = "proof_" + employeeTaskId + "_" + UUID.randomUUID() + "." + ext;
        Path targetPath = uploadPath.resolve(uniqueFilename);
        Files.copy(proofFile.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Update task
        et.setStatus("COMPLETED");
        et.setProofFilePath(uniqueFilename);

        EmployeeTask saved = employeeTaskRepository.save(et);

        // Log activity with employee context
        String bonusNote = et.getTask().isBonus() ? " (Bonus task — ₹500 bonus earned)" : "";
        activityLogService.log("TASK_COMPLETED",
                et.getEmployee().getFullName() + " completed task: '" + et.getTask().getTitle() + "'" + bonusNote,
                et.getEmployee());

        return EmployeeTaskResponse.fromEntity(saved);
    }


    /**
     * HR marks a task as COMPLETED without requiring proof file.
     */
    @Transactional
    public EmployeeTaskResponse markTaskDoneByHR(Long assignmentId) {
        EmployeeTask et = employeeTaskRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assigned task not found: " + assignmentId));

        et.setStatus("COMPLETED");
        EmployeeTask saved = employeeTaskRepository.save(et);

        activityLogService.logEmployee("TASK_COMPLETED",
                "HR marked task '" + et.getTask().getTitle() + "' as DONE for " + et.getEmployee().getFullName(),
                et.getEmployee());

        return EmployeeTaskResponse.fromEntity(saved);
    }

        // ===================== QUERIES =====================

    /**
     * Get all tasks assigned to an employee
     */
    public List<EmployeeTaskResponse> getTasksForEmployee(Long empId) {
        return employeeTaskRepository.findByEmpId(empId)
                .stream()
                .map(EmployeeTaskResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Count completed bonus tasks for an employee — for dashboard bonus display
     */
    public long countCompletedBonusTasks(Long empId) {
        return employeeTaskRepository.countCompletedBonusTasksByEmpId(empId);
    }

    /**
     * Calculate total bonus amount earned by an employee
     */
    public BigDecimal calculateBonusAmount(Long empId) {
        long bonusTaskCount = countCompletedBonusTasks(empId);
        return ActivityLogService.BONUS_PER_TASK.multiply(BigDecimal.valueOf(bonusTaskCount));
    }
}
