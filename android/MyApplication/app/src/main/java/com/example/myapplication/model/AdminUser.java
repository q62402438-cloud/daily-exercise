package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdminUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("adminId")
    private String adminId;

    @SerializedName("adminName")
    private String adminName;

    @SerializedName("adminPassword")
    private String adminPassword;

    @SerializedName("role")
    private String role;

    public AdminUser() {
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}