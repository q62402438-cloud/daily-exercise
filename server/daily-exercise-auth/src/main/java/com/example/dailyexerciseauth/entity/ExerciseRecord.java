package com.example.dailyexerciseauth.entity;

import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExerciseRecord {
    private Integer recordID;
    private Integer userID;
    private String sportsDate;
    private Integer eventID;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer exerciseDuration;
    private Float exerciseAmount;
    private Float calorie;
    private Integer recordType;
    private String sportName;
    private String planName;
}
