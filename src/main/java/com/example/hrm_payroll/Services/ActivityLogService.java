package com.example.hrm_payroll.Services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.hrm_payroll.DTO.ActivityLogResponse;
import com.example.hrm_payroll.Entity.ActivityLog;
import com.example.hrm_payroll.Entity.ActivityLogRole;
import com.example.hrm_payroll.Entity.Employee;
import com.example.hrm_payroll.Repository.ActivityLogRepository;

@Service
public class ActivityLogService {

    public static final java.math.BigDecimal BONUS_PER_TASK = new java.math.BigDecimal("500.00");

    @Autowired
    private ActivityLogRepository activityLogRepository;

    // ── HR action (no employee context) ───────────────────────
    @Transactional
    public void logHR(String actionType, String description) {
        activityLogRepository.save(
            new ActivityLog(actionType, description, ActivityLogRole.HR)
        );
    }

    // ── Employee action (with employee context) ────────────────
    @Transactional
    public void logEmployee(String actionType, String description, Employee employee) {
        activityLogRepository.save(
            new ActivityLog(actionType, description, ActivityLogRole.EMPLOYEE, employee)
        );
    }

    // ── Legacy log() kept for backward-compat with existing callers ──
    @Transactional
    public void log(String actionType, String description) {
        logHR(actionType, description);
    }

    @Transactional
    public void log(String actionType, String description, Employee employee) {
        if (employee == null) {
            logHR(actionType, description);
        } else {
            logEmployee(actionType, description, employee);
        }
    }

    // ── HR dashboard: last N HR-role activities ────────────────
    public List<ActivityLogResponse> getRecentHRActivities(int limit) {
        return activityLogRepository
                .findRecentByRole(ActivityLogRole.HR, PageRequest.of(0, limit))
                .stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Employee dashboard: last N activities for that employee ─
    public List<ActivityLogResponse> getRecentActivitiesForEmployee(Long empId, int limit) {
        return activityLogRepository
                .findRecentByEmpId(empId, PageRequest.of(0, limit))
                .stream()
                .map(ActivityLogResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // ── Legacy kept for ActivityController backward-compat ──────
    public List<ActivityLogResponse> getRecentActivities(int limit) {
        return getRecentHRActivities(limit);
    }
}
