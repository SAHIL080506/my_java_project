package com.example.hrm_payroll.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hrm_payroll.Entity.ActivityLog;
import com.example.hrm_payroll.Entity.ActivityLogRole;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Last N HR-role activities (for HR dashboard)
    @Query("SELECT a FROM ActivityLog a WHERE a.role = :role ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentByRole(@Param("role") ActivityLogRole role,
            org.springframework.data.domain.Pageable pageable);

    // Last N activities for a specific employee (for employee dashboard)
    @Query("SELECT a FROM ActivityLog a WHERE a.employee.empId = :empId ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentByEmpId(@Param("empId") Long empId,
            org.springframework.data.domain.Pageable pageable);

    // All recent activities regardless of role (fallback)
    @Query("SELECT a FROM ActivityLog a ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentActivities(org.springframework.data.domain.Pageable pageable);

    // Employee activities by empId with pageable (kept for compatibility)
    @Query("SELECT a FROM ActivityLog a WHERE a.employee.empId = :empId ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentActivitiesByEmpId(@Param("empId") Long empId,
            org.springframework.data.domain.Pageable pageable);
}
