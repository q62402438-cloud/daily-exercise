package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class CommentEntity {
    @SerializedName(value = "commentID", alternate = {"commentId"})
    private Integer commentID;

    @SerializedName("postID")
    private Integer postID;

    @SerializedName("userID")
    private Integer userID;

    @SerializedName("userName")
    private String userName;

    @SerializedName("content")
    private String content;

    @SerializedName("publishTime")
    private String publishTime;

    public Integer getCommentID() {
        return commentID;
    }

    public void setCommentID(Integer commentID) {
        this.commentID = commentID;
    }

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }
}
