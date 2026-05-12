package com.example.dailyexerciseauth.entity;

import lombok.Data;

@Data
public class User {
    private Integer userID;       // 主键
    private String userPassword;  // 密码
    private Integer userType;     // 1-普通用户 0-管理员
    private String userName;      // 用户名
    private String phoneNumber;   // 手机号
    
    public User() {
    }
    
    public void setUserID(Integer userID) {
        this.userID = userID;
    }
    
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
    
    public void setUserType(Integer userType) {
        this.userType = userType;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public Integer getUserID() {
        return userID;
    }
    
    public String getUserPassword() {
        return userPassword;
    }
    
    public Integer getUserType() {
        return userType;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
