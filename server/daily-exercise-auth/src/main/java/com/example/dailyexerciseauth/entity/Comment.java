package com.example.dailyexerciseauth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer commentID;
    private Integer postID;
    private Integer userID;
    private String content;
    private LocalDateTime publishTime;
    private String userName;
}
