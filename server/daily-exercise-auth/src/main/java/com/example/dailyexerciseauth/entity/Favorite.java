package com.example.dailyexerciseauth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Integer favoriteID;
    private Integer userID;
    private Integer targetID;
    private Integer targetType;
    private String linkUrl;
    private LocalDateTime favoriteTime;
}
