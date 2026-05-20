package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPostId(Integer postID);
    Comment getCommentById(Integer commentID);
    boolean addComment(Comment comment);
    boolean updateComment(Comment comment);
    boolean deleteComment(Integer commentID);
}
