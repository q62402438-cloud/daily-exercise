package com.example.myapplication;

public class Plan {
    private String planId;
    private String planName;
    private String sportType;
    private String startDate;
    private String endDate;
    private String dailyExercise;
    private String dailyCalorie;
    private boolean isPublic;

    public Plan(String planId, String planName, String sportType, String startDate,
                String endDate, String dailyExercise, String dailyCalorie, boolean isPublic) {
        this.planId = planId;
        this.planName = planName;
        this.sportType = sportType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailyExercise = dailyExercise;
        this.dailyCalorie = dailyCalorie;
        this.isPublic = isPublic;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
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

    public String getDailyExercise() {
        return dailyExercise;
    }

    public void setDailyExercise(String dailyExercise) {
        this.dailyExercise = dailyExercise;
    }

    public String getDailyCalorie() {
        return dailyCalorie;
    }

    public void setDailyCalorie(String dailyCalorie) {
        this.dailyCalorie = dailyCalorie;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}