package com.example.hrm_payroll.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.hrm_payroll.Entity.Payroll;

public class PayrollResponse {
    
    private Long payrollId;
    private Long empId;
    private String employeeName;
    private String designation;
    private String department;
    private BigDecimal basicSalary;
    private BigDecimal hra;
    private BigDecimal da;
    private BigDecimal pf;
    private BigDecimal tax;
    private BigDecimal bonus;
    private BigDecimal netSalary;
    private String salaryMonth;
    private LocalDateTime generatedDate;

    public PayrollResponse() {}

    // Convert Payroll entity to PayrollResponse
    public static PayrollResponse fromEntity(Payroll payroll) {
        PayrollResponse response = new PayrollResponse();
        response.setPayrollId(payroll.getPayrollId());
        response.setEmpId(payroll.getEmployee().getEmpId());
        response.setEmployeeName(payroll.getEmployee().getFullName());
        response.setDesignation(payroll.getEmployee().getDesignation());
        response.setDepartment(payroll.getEmployee().getDepartment());
        response.setBasicSalary(payroll.getBasicSalary());
        response.setHra(payroll.getHra());
        response.setDa(payroll.getDa());
        response.setPf(payroll.getPf());
        response.setTax(payroll.getTax());
        response.setBonus(payroll.getBonus() != null ? payroll.getBonus() : BigDecimal.ZERO);
        response.setNetSalary(payroll.getNetSalary());
        response.setSalaryMonth(payroll.getSalaryMonth());
        response.setGeneratedDate(payroll.getGeneratedDate());
        return response;
    }

    public Long getPayrollId() { return payrollId; }
    public void setPayrollId(Long payrollId) { this.payrollId = payrollId; }

    public Long getEmpId() { return empId; }
    public void setEmpId(Long empId) { this.empId = empId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getBasicSalary() { return basicSalary; }
    public void setBasicSalary(BigDecimal basicSalary) { this.basicSalary = basicSalary; }

    public BigDecimal getHra() { return hra; }
    public void setHra(BigDecimal hra) { this.hra = hra; }

    public BigDecimal getDa() { return da; }
    public void setDa(BigDecimal da) { this.da = da; }

    public BigDecimal getPf() { return pf; }
    public void setPf(BigDecimal pf) { this.pf = pf; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getNetSalary() { return netSalary; }
    public void setNetSalary(BigDecimal netSalary) { this.netSalary = netSalary; }

    public String getSalaryMonth() { return salaryMonth; }
    public void setSalaryMonth(String salaryMonth) { this.salaryMonth = salaryMonth; }

    public LocalDateTime getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDateTime generatedDate) { this.generatedDate = generatedDate; }
}