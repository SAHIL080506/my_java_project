package com.example.hrm_payroll.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hrm_payroll.Entity.LeaveRequest;
import com.example.hrm_payroll.Entity.LeaveStatus;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {

    // All leaves for a specific employee (with employee + user fetched)
   @Query("SELECT lr FROM LeaveRequest lr WHERE lr.employee.empId = :empId ORDER BY lr.appliedDate DESC")
List<LeaveRequest> findByEmployeeId(@Param("empId") Long empId);

    // All leaves by status
    List<LeaveRequest> findByStatus(LeaveStatus status);

    // Count pending leave requests
    long countByStatus(LeaveStatus status);

    // Approved leaves overlapping a date range (for attendance calculation)
    @Query("SELECT lr FROM LeaveRequest lr " +
           "WHERE lr.status = :status " +
           "AND ((lr.startDate BETWEEN :startDate AND :endDate) " +
           "OR (lr.endDate BETWEEN :startDate AND :endDate) " +
           "OR (lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    List<LeaveRequest> findApprovedLeavesInDateRange(
        @Param("status") LeaveStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // 🔥 IMPORTANT: Prevent overlapping leave requests
    @Query("SELECT COUNT(lr) > 0 FROM LeaveRequest lr " +
           "WHERE lr.employee.empId = :empId " +
           "AND lr.status <> 'REJECTED' " +
           "AND ((lr.startDate BETWEEN :startDate AND :endDate) " +
           "OR (lr.endDate BETWEEN :startDate AND :endDate) " +
           "OR (lr.startDate <= :startDate AND lr.endDate >= :endDate))")
    boolean existsOverlappingLeave(
        @Param("empId") Long empId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // All leave requests ordered by applied date (with fetch)
    @Query("SELECT lr FROM LeaveRequest lr " +
           "LEFT JOIN FETCH lr.employee e " +
           "LEFT JOIN FETCH e.user " +
           "ORDER BY lr.appliedDate DESC")
    List<LeaveRequest> findAllOrderByAppliedDateDesc();
}