package com.example.hrm_payroll.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.Payroll;

public interface PayrollRepository extends JpaRepository<Payroll, Long> {

    // Find payroll by employee ID
    @Query("SELECT p FROM Payroll p WHERE p.employee.empId = :empId")
    List<Payroll> findByEmployeeId(@Param("empId") Long empId);

    // Find payroll by salary month
    List<Payroll> findBySalaryMonth(String salaryMonth);

    // Total payroll for a month
    @Query("SELECT COALESCE(SUM(p.netSalary), 0) FROM Payroll p WHERE p.salaryMonth = :salaryMonth")
    BigDecimal getMonthlyPayrollTotal(@Param("salaryMonth") String salaryMonth);

    // Check if payroll exists (prevent duplicate generation)
    Optional<Payroll> findByEmployeeAndSalaryMonth(Employee employee, String salaryMonth);

    // IDs of employees who already have payroll for a given month
    @Query("SELECT p.employee.empId FROM Payroll p WHERE p.salaryMonth = :salaryMonth")
    List<Long> findEmpIdsWithPayrollForMonth(@Param("salaryMonth") String salaryMonth);

    // Get payroll for specific employee + month
    @Query("SELECT p FROM Payroll p WHERE p.employee.empId = :empId AND p.salaryMonth = :salaryMonth")
    Optional<Payroll> findByEmpIdAndSalaryMonth(
        @Param("empId") Long empId,
        @Param("salaryMonth") String salaryMonth
    );
}