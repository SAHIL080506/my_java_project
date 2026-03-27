package com.example.hrm_payroll.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.hrm_payroll.Entity.OnboardingTask;

@Repository
public interface OnboardingTaskRepository extends JpaRepository<OnboardingTask, Long> {
}
