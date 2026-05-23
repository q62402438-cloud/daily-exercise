package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class TrainingPlan {

    @SerializedName(value = "planID", alternate = {"planId"})
    private Integer planID;

    @SerializedName("planName")
    private String planName;

    @SerializedName(value = "sportName", alternate = {"sportType"})
    private String sportName;

    @SerializedName(value = "startTime", alternate = {"startDate"})
    private String startTime;

    @SerializedName(value = "endTime", alternate = {"endDate"})
    private String endTime;

    @SerializedName(value = "exerciseAmount", alternate = {"dailyExercise"})
    private String exerciseAmount;

    @SerializedName(value = "dailyCalorie", alternate = {"percentage"})
    private String dailyCalorie;

    @SerializedName("planType")
    private Integer planType;

    @SerializedName(value = "userID", alternate = {"userId"})
    private Integer userID;

    @SerializedName("detail")
    private String detail;

    public TrainingPlan() {
    }

    public Integer getPlanID() {
        return planID;
    }

    public void setPlanID(Integer planID) {
        this.planID = planID;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getExerciseAmount() {
        return exerciseAmount;
    }

    public void setExerciseAmount(String exerciseAmount) {
        this.exerciseAmount = exerciseAmount;
    }

    public String getDailyCalorie() {
        return dailyCalorie;
    }

    public void setDailyCalorie(String dailyCalorie) {
        this.dailyCalorie = dailyCalorie;
    }

    public Integer getPlanType() {
        return planType;
    }

    public void setPlanType(Integer planType) {
        this.planType = planType;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    // Backward-compatible aliases for old call sites.
    public Integer getPlanId() {
        return getPlanID();
    }

    public void setPlanId(Integer planId) {
        setPlanID(planId);
    }

    public String getSportType() {
        return getSportName();
    }

    public void setSportType(String sportType) {
        setSportName(sportType);
    }

    public String getStartDate() {
        return getStartTime();
    }

    public void setStartDate(String startDate) {
        setStartTime(startDate);
    }

    public String getEndDate() {
        return getEndTime();
    }

    public void setEndDate(String endDate) {
        setEndTime(endDate);
    }

    public String getDailyExercise() {
        return getExerciseAmount();
    }

    public void setDailyExercise(String dailyExercise) {
        setExerciseAmount(dailyExercise);
    }

    public Integer getUserId() {
        return getUserID();
    }

    public void setUserId(Integer userId) {
        setUserID(userId);
    }
}