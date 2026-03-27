package com.example.hrm_payroll.DTO;

import com.example.hrm_payroll.Entity.OnboardingTask;

public class OnboardingTaskResponse {

    private Long taskId;
    private String title;
    private String description;
    private boolean isBonus;

    public OnboardingTaskResponse() {}

    public static OnboardingTaskResponse fromEntity(OnboardingTask task) {
        OnboardingTaskResponse dto = new OnboardingTaskResponse();
        dto.setTaskId(task.getTaskId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setBonus(task.isBonus());
        return dto;
    }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isBonus() { return isBonus; }
    public void setBonus(boolean bonus) { isBonus = bonus; }
}
