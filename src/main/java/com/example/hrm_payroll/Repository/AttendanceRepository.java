package com.example.hrm_payroll.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hrm_payroll.Entity.Attendance;
import com.example.hrm_payroll.Entity.AttendanceStatus;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /** Find attendance record for a specific employee on a specific date */
    @Query("SELECT a FROM Attendance a WHERE a.employee.empId = :empId AND a.date = :date")
    Optional<Attendance> findByEmpIdAndDate(@Param("empId") Long empId, @Param("date") LocalDate date);

    /** Count employees by status for a specific date */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.status = :status")
    long countByDateAndStatus(@Param("date") LocalDate date, @Param("status") AttendanceStatus status);

    /** Get all attendance records for a specific date */
    @Query("SELECT a FROM Attendance a WHERE a.date = :date")
    List<Attendance> findByDate(@Param("date") LocalDate date);

    /** Get full attendance history for an employee */
    @Query("SELECT a FROM Attendance a WHERE a.employee.empId = :empId ORDER BY a.date DESC")
    List<Attendance> findByEmpId(@Param("empId") Long empId);

    /**
     * Count ACTIVE employees today:
     * check_in_time IS NOT NULL AND check_out_time IS NULL
     */
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.date = :date AND a.checkInTime IS NOT NULL AND a.checkOutTime IS NULL")
    long countActiveToday(@Param("date") LocalDate date);
}
