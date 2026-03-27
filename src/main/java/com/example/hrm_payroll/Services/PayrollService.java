package com.example.hrm_payroll.Services;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hrm_payroll.DTO.GeneratePayrollRequest;
import com.example.hrm_payroll.DTO.PayrollResponse;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.Payroll;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.EmployeeTaskRepository;
import com.example.hrm_payroll.Repository.PayrollRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

@Service
public class PayrollService {

    private static final BigDecimal BONUS_PER_TASK = new BigDecimal("500");

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeTaskRepository employeeTaskRepository;

    /**
     * Generate (or update) payroll for an employee.
     * If payroll already exists for the month, bonus and net_salary are updated.
     */
    public PayrollResponse generatePayroll(GeneratePayrollRequest request) {

        validateSalaryMonth(request.getSalaryMonth());

        // Find employee
        Employee employee = employeeRepository.findById(request.getEmpId())
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + request.getEmpId()));

        // Validate input amounts
        validateAmounts(request);

        // Calculate salary components
        BigDecimal basicSalary = request.getBasicSalary();
        BigDecimal hra = request.getHra();
        BigDecimal da = request.getDa();

        // Calculate PF amount (pf percentage / 100 * basic salary)
        BigDecimal pfAmount = basicSalary
            .multiply(request.getPf())
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Calculate Tax amount (tax percentage / 100 * basic salary)
        BigDecimal taxAmount = basicSalary
            .multiply(request.getTax())
            .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        // Calculate bonus from completed bonus tasks
        long completedBonusTasks = employeeTaskRepository.countCompletedBonusTasksByEmpId(request.getEmpId());
        BigDecimal bonusAmount = BONUS_PER_TASK.multiply(new BigDecimal(completedBonusTasks));

        // Calculate net salary: basic + hra + da + bonus - pf - tax
        BigDecimal netSalary = basicSalary
            .add(hra)
            .add(da)
            .add(bonusAmount)
            .subtract(pfAmount)
            .subtract(taxAmount);

        // Check if payroll already exists for this employee and month
        Optional<Payroll> existingPayroll = payrollRepository.findByEmployeeAndSalaryMonth(
            employee, request.getSalaryMonth()
        );

        Payroll payroll;
        if (existingPayroll.isPresent()) {
            // Update existing payroll — only bonus and net_salary change
            payroll = existingPayroll.get();
            payroll.setBasicSalary(basicSalary);
            payroll.setHra(hra);
            payroll.setDa(da);
            payroll.setPf(pfAmount);
            payroll.setTax(taxAmount);
            payroll.setBonus(bonusAmount);
            payroll.setNetSalary(netSalary);
            payroll.setGeneratedDate(LocalDateTime.now());
        } else {
            // Create new payroll record
            payroll = new Payroll();
            payroll.setEmployee(employee);
            payroll.setBasicSalary(basicSalary);
            payroll.setHra(hra);
            payroll.setDa(da);
            payroll.setPf(pfAmount);
            payroll.setTax(taxAmount);
            payroll.setBonus(bonusAmount);
            payroll.setNetSalary(netSalary);
            payroll.setSalaryMonth(request.getSalaryMonth());
            payroll.setGeneratedDate(LocalDateTime.now());
        }

        // Save payroll
        Payroll savedPayroll = payrollRepository.save(payroll);

        return PayrollResponse.fromEntity(savedPayroll);
    }

    //Check if payroll exists for current employee and month
    public boolean payrollExists(Long empId, String salaryMonth) {
        Employee employee = employeeRepository.findById(empId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

        return payrollRepository.findByEmployeeAndSalaryMonth(employee, salaryMonth).isPresent();
    }

    //Generate PDF payslip for specific employee and month
    public byte[] generatePayslipPDF(Long empId, String salaryMonth) {

        Employee employee = employeeRepository.findById(empId)
            .orElseThrow(() -> new IllegalArgumentException("Employee not found with ID: " + empId));

        // Find payroll
        Payroll payroll = payrollRepository.findByEmployeeAndSalaryMonth(employee, salaryMonth)
            .orElseThrow(() -> new IllegalArgumentException(
                "Payroll not found for employee " + employee.getFullName() +
                " for month " + salaryMonth
            ));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Company header
            Paragraph header = new Paragraph("HR MANAGEMENT SYSTEM")
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
            document.add(header);

            Paragraph subHeader = new Paragraph("Salary Slip")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
            document.add(subHeader);

            document.add(new Paragraph("\n"));

            // Employee details
            document.add(new Paragraph("Employee Name: " + employee.getFullName()).setFontSize(12));
            document.add(new Paragraph("Employee ID: " + employee.getEmpId()).setFontSize(12));
            document.add(new Paragraph("Designation: " + (employee.getDesignation() != null ? employee.getDesignation() : "N/A")).setFontSize(12));
            document.add(new Paragraph("Department: " + (employee.getDepartment() != null ? employee.getDepartment() : "N/A")).setFontSize(12));
            document.add(new Paragraph("Salary Month: " + formatMonthForDisplay(salaryMonth)).setFontSize(12));
            document.add(new Paragraph("\n"));

            // Salary details table
            Table table = new Table(UnitValue.createPercentArray(new float[]{3, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            // Earnings section
            table.addCell(createCell("EARNINGS", true));
            table.addCell(createCell("AMOUNT (₹)", true));

            table.addCell(createCell("Basic Salary", false));
            table.addCell(createCell(formatAmount(payroll.getBasicSalary()), false));

            table.addCell(createCell("HRA", false));
            table.addCell(createCell(formatAmount(payroll.getHra()), false));

            table.addCell(createCell("DA", false));
            table.addCell(createCell(formatAmount(payroll.getDa()), false));

            BigDecimal bonus = payroll.getBonus() != null ? payroll.getBonus() : BigDecimal.ZERO;
            table.addCell(createCell("Bonus", false));
            table.addCell(createCell(formatAmount(bonus), false));

            BigDecimal grossSalary = payroll.getBasicSalary().add(payroll.getHra()).add(payroll.getDa()).add(bonus);
            table.addCell(createCell("Gross Salary", true));
            table.addCell(createCell(formatAmount(grossSalary), true));

            // Deductions section
            table.addCell(createCell("DEDUCTIONS", true));
            table.addCell(createCell("AMOUNT (₹)", true));

            table.addCell(createCell("PF", false));
            table.addCell(createCell(formatAmount(payroll.getPf()), false));

            table.addCell(createCell("Tax", false));
            table.addCell(createCell(formatAmount(payroll.getTax()), false));

            BigDecimal totalDeductions = payroll.getPf().add(payroll.getTax());
            table.addCell(createCell("Total Deductions", true));
            table.addCell(createCell(formatAmount(totalDeductions), true));

            // Net salary
            table.addCell(createCell("NET SALARY", true));
            table.addCell(createCell(formatAmount(payroll.getNetSalary()), true));

            document.add(table);

            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Generated on: " +
                payroll.getGeneratedDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.RIGHT));

            document.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating payslip PDF: " + e.getMessage(), e);
        }
    }

    //Create table cell with formatting
    private com.itextpdf.layout.element.Cell createCell(String content, boolean isBold) {
        com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell()
            .add(new Paragraph(content));

        if (isBold) {
            cell.setBold();
        }

        return cell;
    }

    //Format amount for display
    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }

    //Format month from YYYY-MM to readable format
    private String formatMonthForDisplay(String salaryMonth) {
        try {
            YearMonth ym = YearMonth.parse(salaryMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
            return ym.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
        } catch (Exception e) {
            return salaryMonth;
        }
    }

    //Validate salary month format
    private void validateSalaryMonth(String salaryMonth) {
        if (salaryMonth == null || salaryMonth.trim().isEmpty()) {
            throw new IllegalArgumentException("Salary month is required");
        }

        try {
            YearMonth.parse(salaryMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid salary month format. Expected format: YYYY-MM (e.g., 2026-02)");
        }
    }

    //Validate amounts
    private void validateAmounts(GeneratePayrollRequest request) {
        if (request.getBasicSalary() == null || request.getBasicSalary().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Basic salary must be greater than zero");
        }

        if (request.getHra() == null || request.getHra().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("HRA cannot be negative");
        }

        if (request.getDa() == null || request.getDa().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("DA cannot be negative");
        }

        if (request.getPf() == null || request.getPf().compareTo(BigDecimal.ZERO) < 0 ||
            request.getPf().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("PF percentage must be between 0 and 100");
        }

        if (request.getTax() == null || request.getTax().compareTo(BigDecimal.ZERO) < 0 ||
            request.getTax().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Tax percentage must be between 0 and 100");
        }
    }
}