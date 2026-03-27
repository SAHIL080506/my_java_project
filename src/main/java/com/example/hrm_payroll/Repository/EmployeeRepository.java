package com.example.hrm_payroll.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.UserStatus;
import com.example.hrm_payroll.Entity.Users;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUser(Users user);

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user")
    List<Employee> findAllWithUser();

    // ACTIVE-only search (used by HR employee list)
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user u WHERE " +
           "u.status = :status AND " +
           "(:name IS NULL OR :name = '' OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(e.middleName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:department IS NULL OR :department = '' OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) " +
           "AND (:designation IS NULL OR :designation = '' OR LOWER(e.designation) LIKE LOWER(CONCAT('%', :designation, '%')))")
    List<Employee> searchActiveEmployees(
        @Param("name") String name,
        @Param("department") String department,
        @Param("designation") String designation,
        @Param("status") UserStatus status
    );

    // Legacy — all statuses
    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user WHERE " +
           "(:name IS NULL OR :name = '' OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(e.middleName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:department IS NULL OR :department = '' OR LOWER(e.department) LIKE LOWER(CONCAT('%', :department, '%'))) " +
           "AND (:designation IS NULL OR :designation = '' OR LOWER(e.designation) LIKE LOWER(CONCAT('%', :designation, '%')))")
    List<Employee> searchEmployees(
        @Param("name") String name,
        @Param("department") String department,
        @Param("designation") String designation
    );

    @Query("SELECT DISTINCT e.department FROM Employee e WHERE e.department IS NOT NULL")
    List<String> findAllDepartments();

    @Query("SELECT DISTINCT e.designation FROM Employee e WHERE e.designation IS NOT NULL")
    List<String> findAllDesignations();

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.user " +
           "WHERE YEAR(e.joiningDate) = YEAR(CURRENT_TIMESTAMP) " +
           "AND MONTH(e.joiningDate) = MONTH(CURRENT_TIMESTAMP) " +
           "ORDER BY e.joiningDate DESC")
    List<Employee> findNewEmployeesThisMonth();
}
