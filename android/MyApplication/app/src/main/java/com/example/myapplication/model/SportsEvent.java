package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SportsEvent implements Serializable {

    @SerializedName("eventID")
    private Integer eventID;

    @SerializedName("sportName")
    private String sportName;

    @SerializedName("averageCalorie")
    private Float averageCalorie;

    public SportsEvent() {
    }

    public SportsEvent(Integer eventID, String sportName, Float averageCalorie) {
        this.eventID = eventID;
        this.sportName = sportName;
        this.averageCalorie = averageCalorie;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getSportName() {
        return sportName;
    }

    public void setSportName(String sportName) {
        this.sportName = sportName;
    }

    public Float getAverageCalorie() {
        return averageCalorie;
    }

    public void setAverageCalorie(Float averageCalorie) {
        this.averageCalorie = averageCalorie;
    }

    @Override
    public String toString() {
        return sportName + " (" + averageCalorie + " kcal/小时)";
    }
}