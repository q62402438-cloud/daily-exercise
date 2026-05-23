package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ExerciseRecord implements Serializable {

    @SerializedName("recordID")
    private Integer recordID;

    @SerializedName("userID")
    private Integer userID;

    @SerializedName("sportsDate")
    private String sportsDate;

    @SerializedName("eventID")
    private Integer eventID;

    @SerializedName("planID")
    private Integer planID;

    @SerializedName("sportName")
    private String sportName;

    @SerializedName("startTime")
    private String startTime;

    @SerializedName("endTime")
    private String endTime;

    @SerializedName("exerciseDuration")
    private Integer exerciseDuration;

    @SerializedName("exerciseAmount")
    private Float exerciseAmount;

    @SerializedName("calorie")
    private Integer calorie;

    @SerializedName("recordType")
    private Integer recordType;

    @SerializedName("startDate")
    private String startDate;

    @SerializedName("endDate")
    private String endDate;

    public ExerciseRecord() {
    }

    public Integer getRecordID() {
        return recordID;
    }

    public void setRecordID(Integer recordID) {
        this.recordID = recordID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getSportsDate() {
        return sportsDate;
    }

    public void setSportsDate(String sportsDate) {
        this.sportsDate = sportsDate;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public Integer getPlanID() {
        return planID;
    }

    public void setPlanID(Integer planID) {
        this.planID = planID;
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

    public Integer getExerciseDuration() {
        return exerciseDuration;
    }

    public void setExerciseDuration(Integer exerciseDuration) {
        this.exerciseDuration = exerciseDuration;
    }

    public Float getExerciseAmount() {
        return exerciseAmount;
    }

    public void setExerciseAmount(Float exerciseAmount) {
        this.exerciseAmount = exerciseAmount;
    }

    public Integer getCalorie() {
        return calorie;
    }

    public void setCalorie(Integer calorie) {
        this.calorie = calorie;
    }

    public Integer getRecordType() {
        return recordType;
    }

    public void setRecordType(Integer recordType) {
        this.recordType = recordType;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}