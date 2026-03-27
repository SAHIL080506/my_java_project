package com.example.hrm_payroll.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.hrm_payroll.Entity.RoleType;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {

    // Login
    Optional<Users> findByUsername(String username);

    // Validation
    boolean existsByUsername(String username);

    // Email support (important for future-proofing)
    Optional<Users> findByEmail(String email);
    boolean existsByEmail(String email);

    // Filters
    List<Users> findByRole(RoleType role);
    List<Users> findByStatus(UserStatus status);
}