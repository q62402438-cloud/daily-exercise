package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.Post;
import com.example.dailyexerciseauth.mapper.PostMapper;
import com.example.dailyexerciseauth.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllApprovedPosts() {
        return postMapper.getAllApprovedPosts();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPostsByAuthorId(Integer authorID) {
        return postMapper.getPostsByAuthorId(authorID);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Integer postID) {
        return postMapper.getPostById(postID);
    }

    @Override
    @Transactional
    public boolean createPost(Post post) {
        post.setPublishTime(LocalDateTime.now());
        if (post.getAuditState() == null) {
            post.setAuditState(0);
        }
        return postMapper.insertPost(post) > 0;
    }

    @Override
    @Transactional
    public boolean updatePost(Post post) {
        return postMapper.updatePost(post) > 0;
    }

    @Override
    @Transactional
    public boolean deletePost(Integer postID) {
        return postMapper.deletePost(postID) > 0;
    }

    @Override
    @Transactional
    public boolean auditPost(Integer postID, Integer auditState) {
        return postMapper.updateAuditState(postID, auditState) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Post> getPendingPosts() {
        return postMapper.getPendingPosts();
    }
}
