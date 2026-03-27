package com.example.hrm_payroll.Services;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hrm_payroll.DTO.EmployeeDashboardResponse;
import com.example.hrm_payroll.DTO.EmployeeTaskResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.Payroll;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.EmployeeTaskRepository;
import com.example.hrm_payroll.Repository.PayrollRepository;

@Service
public class EmployeeDashboardService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private PayrollRepository payrollRepository;
    @Autowired private EmployeeTaskRepository employeeTaskRepository;
    @Autowired private TaskService taskService;

    //Build full dashboard data for a logged-in employee
    public EmployeeDashboardResponse getDashboardForEmployee(Long empId) {
        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));

        String monthStr = YearMonth.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // Salary info
        Optional<Payroll> payroll = payrollRepository.findByEmpIdAndSalaryMonth(empId, monthStr);
        BigDecimal currentMonthSalary = payroll.map(Payroll::getNetSalary).orElse(null);
        boolean payrollGenerated = payroll.isPresent();

        // Task stats
        long total = employeeTaskRepository.findByEmpId(empId).size();
        long completed = employeeTaskRepository.countByEmpIdAndStatus(empId, "COMPLETED");
        long pending = employeeTaskRepository.countByEmpIdAndStatus(empId, "PENDING");
        long bonusCompleted = taskService.countCompletedBonusTasks(empId);
        BigDecimal bonusAmount = taskService.calculateBonusAmount(empId);

        // Tasks list
        List<EmployeeTaskResponse> tasks = taskService.getTasksForEmployee(empId);

        EmployeeDashboardResponse response = new EmployeeDashboardResponse();
        response.setEmpId(employee.getEmpId());
        response.setEmployeeName(employee.getFullName());
        response.setDesignation(employee.getDesignation());
        response.setDepartment(employee.getDepartment());
        response.setCurrentMonthSalary(currentMonthSalary);
        response.setPayrollGenerated(payrollGenerated);
        response.setCurrentMonth(monthStr);
        response.setTotalTasksAssigned(total);
        response.setCompletedTasks(completed);
        response.setPendingTasks(pending);
        response.setCompletedBonusTasks(bonusCompleted);
        response.setBonusAmount(bonusAmount);
        response.setTasks(tasks);

        return response;
    }
}
