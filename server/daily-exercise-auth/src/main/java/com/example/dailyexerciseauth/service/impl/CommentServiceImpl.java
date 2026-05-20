package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.Comment;
import com.example.dailyexerciseauth.mapper.CommentMapper;
import com.example.dailyexerciseauth.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByPostId(Integer postID) {
        return commentMapper.getCommentsByPostId(postID);
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(Integer commentID) {
        return commentMapper.getCommentById(commentID);
    }

    @Override
    @Transactional
    public boolean addComment(Comment comment) {
        comment.setPublishTime(LocalDateTime.now());
        return commentMapper.insertComment(comment) > 0;
    }

    @Override
    @Transactional
    public boolean updateComment(Comment comment) {
        return commentMapper.updateComment(comment) > 0;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentID) {
        return commentMapper.deleteComment(commentID) > 0;
    }
}
