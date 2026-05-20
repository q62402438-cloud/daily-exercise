package com.example.dailyexerciseauth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TrainingPlan {
    private Integer planID;
    private String planName;
    private Integer userID;
    private Integer planType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String sportName;
    private Float exerciseAmount;
    private Float percentage;
    private String detail;
}
