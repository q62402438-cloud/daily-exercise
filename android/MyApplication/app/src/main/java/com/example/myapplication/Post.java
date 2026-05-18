package com.example.myapplication;

public class Post {
    private String postId;
    private String authorName;
    private String title;
    private String content;
    private String postTime;
    private String viewCount;
    private String likeCount;
    private String commentCount;
    private int imageResId;

    public Post(String postId, String authorName, String title, String content,
                String postTime, String viewCount, String likeCount, String commentCount) {
        this.postId = postId;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.postTime = postTime;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.imageResId = 0;
    }

    public Post(String postId, String authorName, String title, String content,
                String postTime, String viewCount, String likeCount, String commentCount, int imageResId) {
        this.postId = postId;
        this.authorName = authorName;
        this.title = title;
        this.content = content;
        this.postTime = postTime;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.imageResId = imageResId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
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

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(String likeCount) {
        this.likeCount = likeCount;
    }

    public String getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(String commentCount) {
        this.commentCount = commentCount;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}
