package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.common.TypeConverter;
import com.example.dailyexerciseauth.entity.Post;
import com.example.dailyexerciseauth.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/list")
    public Result getAllApprovedPosts() {
        List<Post> posts = postService.getAllApprovedPosts();
        return Result.success(posts);
    }

    @GetMapping("/author/{authorId}")
    public Result getPostsByAuthorId(@PathVariable Integer authorId) {
        List<Post> posts = postService.getPostsByAuthorId(authorId);
        return Result.success(posts);
    }

    @GetMapping("/{id}")
    public Result getPostById(@PathVariable Integer id) {
        Post post = postService.getPostById(id);
        if (post != null) {
            return Result.success(post);
        } else {
            return Result.error("帖子不存在");
        }
    }

    @GetMapping("/pending")
    public Result getPendingPosts() {
        List<Post> posts = postService.getPendingPosts();
        return Result.success(posts);
    }

    @PostMapping("/list")
    public Result getAllApprovedPostsPost(@RequestBody java.util.Map<String, Object> params) {
        List<Post> posts = postService.getAllApprovedPosts();
        return Result.success(posts);
    }

    @PostMapping("/get")
    public Result getPostByIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer postID = TypeConverter.toInteger(params.get("postID"));
        Post post = postService.getPostById(postID);
        if (post != null) {
            return Result.success(post);
        } else {
            return Result.error("帖子不存在");
        }
    }

    @PostMapping("/update")
    public Result updatePostPost(@RequestBody java.util.Map<String, Object> params) {
        Post post = new Post();
        post.setPostID(TypeConverter.toInteger(params.get("postID")));
        post.setTitle((String) params.get("title"));
        post.setContent((String) params.get("content"));
        // 编辑帖子时，强制把审核状态设为未审核
        post.setAuditState(0);
        boolean success = postService.updatePost(post);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    

    @PostMapping("/create")
    public Result createPost(@RequestBody Post post) {
        boolean success = postService.createPost(post);
        if (success) {
            return Result.success("发布成功，等待审核");
        } else {
            return Result.error("发布失败");
        }
    }

    @PutMapping("/update")
    public Result updatePost(@RequestBody Post post) {
        boolean success = postService.updatePost(post);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result deletePost(@PathVariable Integer id) {
        boolean success = postService.deletePost(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/delete")
    public Result deletePostByPostID(@RequestBody java.util.Map<String, Object> params) {
        Integer postID = TypeConverter.toInteger(params.get("postID"));
        if (postID == null) {
            return Result.error("帖子ID不能为空");
        }
        boolean success = postService.deletePost(postID);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/audit")
    public Result auditPost(@RequestBody Map<String, Integer> request) {
        Integer postId = request.get("postID");
        Integer auditState = request.get("auditState");
        if (postId == null || auditState == null) {
            return Result.error("参数不能为空");
        }
        boolean success = postService.auditPost(postId, auditState);
        if (success) {
            String msg = auditState == 1 ? "审核通过" : "审核拒绝";
            return Result.success(msg);
        } else {
            return Result.error("审核失败");
        }
    }
}
