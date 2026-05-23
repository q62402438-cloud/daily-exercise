package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class FavoriteEntity {
    @SerializedName(value = "favoriteID", alternate = {"favoriteId"})
    private Integer favoriteID;

    @SerializedName("userID")
    private Integer userID;

    @SerializedName("targetID")
    private Integer targetID;

    @SerializedName("targetType")
    private Integer targetType;

    @SerializedName("linkUrl")
    private String linkUrl;

    @SerializedName("favoriteTime")
    private String favoriteTime;

    public Integer getFavoriteID() {
        return favoriteID;
    }

    public void setFavoriteID(Integer favoriteID) {
        this.favoriteID = favoriteID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getTargetID() {
        return targetID;
    }

    public void setTargetID(Integer targetID) {
        this.targetID = targetID;
    }

    public Integer getTargetType() {
        return targetType;
    }

    public void setTargetType(Integer targetType) {
        this.targetType = targetType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getFavoriteTime() {
        return favoriteTime;
    }

    public void setFavoriteTime(String favoriteTime) {
        this.favoriteTime = favoriteTime;
    }
}
