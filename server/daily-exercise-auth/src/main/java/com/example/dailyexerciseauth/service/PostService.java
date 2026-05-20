package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.Post;
import java.util.List;

public interface PostService {
    List<Post> getAllApprovedPosts();
    List<Post> getPostsByAuthorId(Integer authorID);
    Post getPostById(Integer postID);
    boolean createPost(Post post);
    boolean updatePost(Post post);
    boolean deletePost(Integer postID);
    boolean auditPost(Integer postID, Integer auditState);
    List<Post> getPendingPosts();
}
