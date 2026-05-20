package com.example.dailyexerciseauth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Integer postID;
    private Integer authorID;
    private String authorName;
    private String title;
    private String content;
    private LocalDateTime publishTime;
    private Integer auditState;
}
