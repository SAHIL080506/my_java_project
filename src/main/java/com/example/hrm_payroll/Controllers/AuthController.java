package com.example.hrm_payroll.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.hrm_payroll.DTO.LoginRequest;
import com.example.hrm_payroll.DTO.RegisterRequest;
import com.example.hrm_payroll.DTO.UserResponse;
import com.example.hrm_payroll.Entity.Users;
import com.example.hrm_payroll.Services.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        Users registeredUser = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.fromEntity(registeredUser));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody LoginRequest request, HttpSession session) {
        Users authenticatedUser = userService.authenticateUser(request.getUsername(), request.getPassword());

        session.setAttribute("userId", authenticatedUser.getUserId());
        session.setAttribute("role", authenticatedUser.getRole());

        return ResponseEntity.ok(UserResponse.fromEntity(authenticatedUser));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Users user = userService.getUserById(userId);
        return ResponseEntity.ok(UserResponse.fromEntity(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }
}
