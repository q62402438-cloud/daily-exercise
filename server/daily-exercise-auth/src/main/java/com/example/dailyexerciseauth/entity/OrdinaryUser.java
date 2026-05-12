package com.example.dailyexerciseauth.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrdinaryUser extends User {
    private String userName;
    private String phoneNumber;
    private String userMailbox;
    private String gender;
    private LocalDateTime birthday;
    private LocalDateTime registerTime;
    private Integer age;
    private Float weight;
    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }
}