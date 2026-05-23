package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class PostEntity {
    @SerializedName(value = "postID", alternate = {"postId"})
    private Integer postID;

    @SerializedName("authorID")
    private Integer authorID;

    @SerializedName("authorName")
    private String authorName;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("publishTime")
    private String publishTime;

    @SerializedName("auditState")
    private Integer auditState;

    @SerializedName("viewCount")
    private Integer viewCount;

    @SerializedName("likeCount")
    private Integer likeCount;

    @SerializedName("commentCount")
    private Integer commentCount;

    public Integer getPostID() {
        return postID;
    }

    public void setPostID(Integer postID) {
        this.postID = postID;
    }

    public Integer getAuthorID() {
        return authorID;
    }

    public void setAuthorID(Integer authorID) {
        this.authorID = authorID;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Integer getAuditState() {
        return auditState;
    }

    public void setAuditState(Integer auditState) {
        this.auditState = auditState;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }
}
