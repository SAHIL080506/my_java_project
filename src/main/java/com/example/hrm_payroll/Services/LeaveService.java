package com.example.hrm_payroll.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hrm_payroll.DTO.LeaveRequestDTO;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Entity.LeaveRequest;
import com.example.hrm_payroll.Entity.LeaveStatus;
import com.example.hrm_payroll.Entity.LeaveType;
import com.example.hrm_payroll.Repository.EmployeeRepository;
import com.example.hrm_payroll.Repository.LeaveRequestRepository;

@Service
public class LeaveService {

    @Autowired private LeaveRequestRepository leaveRequestRepository;
    @Autowired private EmployeeRepository employeeRepository;
    @Autowired private ActivityLogService activityLogService;

    /**
     * Employee applies for leave.
     */
    @Transactional
    public LeaveRequestDTO applyLeave(Long empId, String leaveTypeStr,
                                      LocalDate startDate, LocalDate endDate, String reason) {

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Employee employee = employeeRepository.findById(empId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + empId));

        LeaveType leaveType;
        try {
            leaveType = LeaveType.valueOf(leaveTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid leave type. Must be CASUAL, SICK, or PAID");
        }

        LeaveRequest lr = new LeaveRequest();
        lr.setEmployee(employee);
        lr.setLeaveType(leaveType);
        lr.setStartDate(startDate);
        lr.setEndDate(endDate);
        lr.setReason(reason);
        lr.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRequestRepository.save(lr);

        activityLogService.logEmployee("LEAVE_APPLIED",
                employee.getFullName() + " applied for " + leaveType + " leave (" +
                        startDate + " to " + endDate + ")",
                employee);

        return LeaveRequestDTO.fromEntity(saved);
    }

    /**
     * Get all leave requests for an employee.
     */
    public List<LeaveRequestDTO> getLeavesByEmployee(Long empId) {
        return leaveRequestRepository.findByEmployeeId(empId)
                .stream().map(LeaveRequestDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * HR: Get all leave requests.
     */
    public List<LeaveRequestDTO> getAllLeaves() {
        return leaveRequestRepository.findAllOrderByAppliedDateDesc()
                .stream().map(LeaveRequestDTO::fromEntity).collect(Collectors.toList());
    }

    /**
     * HR: Approve a leave request.
     */
    @Transactional
    public LeaveRequestDTO approveLeave(Long leaveId) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + leaveId));

        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be approved");
        }

        lr.setStatus(LeaveStatus.APPROVED);
        LeaveRequest saved = leaveRequestRepository.save(lr);

        activityLogService.logHR("LEAVE_APPROVED",
                "HR approved leave for " + lr.getEmployee().getFullName() +
                        " (" + lr.getLeaveType() + " | " + lr.getStartDate() + " to " + lr.getEndDate() + ")");

        return LeaveRequestDTO.fromEntity(saved);
    }

    /**
     * HR: Reject a leave request.
     */
    @Transactional
    public LeaveRequestDTO rejectLeave(Long leaveId) {
        LeaveRequest lr = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new IllegalArgumentException("Leave request not found: " + leaveId));

        if (lr.getStatus() != LeaveStatus.PENDING) {
            throw new IllegalStateException("Only PENDING leave requests can be rejected");
        }

        lr.setStatus(LeaveStatus.REJECTED);
        LeaveRequest saved = leaveRequestRepository.save(lr);

        activityLogService.logHR("LEAVE_REJECTED",
                "HR rejected leave for " + lr.getEmployee().getFullName() +
                        " (" + lr.getLeaveType() + " | " + lr.getStartDate() + " to " + lr.getEndDate() + ")");

        return LeaveRequestDTO.fromEntity(saved);
    }

    /**
     * Count pending leave requests (for HR dashboard).
     */
    public long countPendingLeaves() {
        return leaveRequestRepository.countByStatus(LeaveStatus.PENDING);
    }
}
