package com.example.hrm_payroll.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hrm_payroll.Entity.EmployeeTask;

@Repository
public interface EmployeeTaskRepository extends JpaRepository<EmployeeTask, Long> {

    // All tasks assigned to a specific employee
    @Query("SELECT et FROM EmployeeTask et WHERE et.employee.empId = :empId ORDER BY et.assignedDate DESC")
    List<EmployeeTask> findByEmpId(@Param("empId") Long empId);

    // Count tasks by status for an employee (for dashboard stats)
    @Query("SELECT COUNT(et) FROM EmployeeTask et WHERE et.employee.empId = :empId AND et.status = :status")
    long countByEmpIdAndStatus(@Param("empId") Long empId, @Param("status") String status);

    // Check if a specific task is already assigned to an employee (used for reassign logic)
    @Query("SELECT et FROM EmployeeTask et WHERE et.employee.empId = :empId AND et.task.taskId = :taskId")
    Optional<EmployeeTask> findByEmpIdAndTaskId(@Param("empId") Long empId, @Param("taskId") Long taskId);

    // Count completed bonus tasks for payroll bonus calculation
    @Query("SELECT COUNT(et) FROM EmployeeTask et WHERE et.employee.empId = :empId AND et.status = 'COMPLETED' AND et.task.isBonus = TRUE")
    long countCompletedBonusTasksByEmpId(@Param("empId") Long empId);
}