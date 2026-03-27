package com.example.hrm_payroll.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hrm_payroll.DTO.EmployeeFullProfileResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.EmployeeTask;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.EmployeeTaskRepository;

import java.util.List;

@Service
public class HREmployeeViewService {

    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private EmployeeTaskRepository employeeTaskRepository;

    public EmployeeFullProfileResponse getFullProfile(Long empId) {
        Employee employee = employeeRepository.findById(empId)
            .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + empId));

        List<EmployeeTask> tasks = employeeTaskRepository.findByEmpId(empId);

        return EmployeeFullProfileResponse.from(employee, tasks);
    }
}
