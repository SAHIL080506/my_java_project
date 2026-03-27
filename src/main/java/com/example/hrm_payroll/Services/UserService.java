package com.example.hrm_payroll.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hrm_payroll.DTO.RegisterRequest;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ActivityLogService activityLogService;

    // ================= REGISTER (from signup form) =================
    @Transactional
    public Users registerUser(RegisterRequest request) {

        if (request.getUsername() == null || request.getUsername().trim().isEmpty())
            throw new RuntimeException("Username is required");

        if (request.getEmail() == null || request.getEmail().trim().isEmpty())
            throw new RuntimeException("Email is required");

        if (request.getPassword() == null || request.getPassword().trim().isEmpty())
            throw new RuntimeException("Password is required");

        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty())
            throw new RuntimeException("First name is required");

        if (request.getConfirmPassword() != null
                && !request.getPassword().equals(request.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");

        String username = request.getUsername().trim().toLowerCase();
        String email    = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already taken");

        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already exists");

        // Create Users record
        Users newUser = new Users();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole() != null ? request.getRole() : RoleType.EMPLOYEE);
        newUser.setStatus(UserStatus.PENDING);

        Users savedUser = userRepository.save(newUser);

        // Create Employee record — store firstName and lastName from signup form
        Employee employee = new Employee();
        employee.setUser(savedUser);
        employee.setFirstName(request.getFirstName().trim());
        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            employee.setLastName(request.getLastName().trim());
        }
        employee.setProfileCompleted(false);

        employeeRepository.save(employee);

        return savedUser;
    }

    // ================= LOGIN =================
    public Users authenticateUser(String username, String password) {

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            throw new RuntimeException("Invalid password");

        if (user.getStatus() != UserStatus.ACTIVE)
            throw new RuntimeException("User is not active");

        return user;
    }

    // ================= GET USER =================
    public Users getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ================= APPROVE / REJECT =================
    @Transactional
    public Users updateUserStatus(Long userId, UserStatus status) {

        Users user = getUserById(userId);
        user.setStatus(status);

        Users savedUser = userRepository.save(user);

        // On approval: ensure Employee record exists — do NOT overwrite existing names
        if (status == UserStatus.ACTIVE) {

            boolean employeeExists = employeeRepository.findByUser(savedUser).isPresent();

            if (!employeeExists) {
                // Only create a minimal employee record; no auto-generated names
                Employee employee = new Employee();
                employee.setUser(savedUser);
                // Leave firstName/lastName null — user will fill in profile
                // firstName has nullable=false in DB so set a placeholder that user can update
                employee.setFirstName("");
                employee.setProfileCompleted(false);
                employeeRepository.save(employee);
            }

            activityLogService.logHR("USER_APPROVED",
                    "User approved: " + savedUser.getUsername());

        } else if (status == UserStatus.REJECTED) {

            activityLogService.logHR("USER_REJECTED",
                    "User rejected: " + savedUser.getUsername());
        }

        return savedUser;
    }
}
