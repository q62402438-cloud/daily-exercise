package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class User implements Serializable { // 实现Serializable以便通过Intent传递

    @SerializedName("userID")
    private Integer userID;
    
    @SerializedName("userPassword")
    private String userPassword;
    
    @SerializedName("userType")
    private Integer userType;
    
    @SerializedName("userName")
    private String userName;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("verifyCode")
    private String verifyCode;

    public User() {
    }

    // 全参构造函数
    public User(Integer userID, String userPassword, Integer userType, String userName, String phoneNumber) {
        this.userID = userID;
        this.userPassword = userPassword;
        this.userType = userType;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
    }

    // Getter 和 Setter 方法
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}