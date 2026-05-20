package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.common.TypeConverter;
import com.example.dailyexerciseauth.entity.Comment;
import com.example.dailyexerciseauth.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/post/{postId}")
    public Result getCommentsByPostId(@PathVariable Integer postId) {
        List<Comment> comments = commentService.getCommentsByPostId(postId);
        return Result.success(comments);
    }

    @GetMapping("/{id}")
    public Result getCommentById(@PathVariable Integer id) {
        Comment comment = commentService.getCommentById(id);
        if (comment != null) {
            return Result.success(comment);
        } else {
            return Result.error("评论不存在");
        }
    }

    @PostMapping("/listByPost")
    public Result getCommentsByPostIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer postID = TypeConverter.toInteger(params.get("postID"));
        List<Comment> comments = commentService.getCommentsByPostId(postID);
        return Result.success(comments);
    }

    @PostMapping("/get")
    public Result getCommentByIdPost(@RequestBody java.util.Map<String, Object> params) {
        Integer commentID = TypeConverter.toInteger(params.get("commentID"));
        Comment comment = commentService.getCommentById(commentID);
        if (comment != null) {
            return Result.success(comment);
        } else {
            return Result.error("评论不存在");
        }
    }

    @PostMapping("/update")
    public Result updateCommentPost(@RequestBody java.util.Map<String, Object> params) {
        Comment comment = new Comment();
        comment.setCommentID(TypeConverter.toInteger(params.get("commentID")));
        comment.setContent((String) params.get("content"));
        boolean success = commentService.updateComment(comment);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @PostMapping("/delete")
    public Result deleteCommentPost(@RequestBody java.util.Map<String, Object> params) {
        Integer commentID = TypeConverter.toInteger(params.get("commentID"));
        boolean success = commentService.deleteComment(commentID);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        boolean success = commentService.addComment(comment);
        if (success) {
            return Result.success("评论成功");
        } else {
            return Result.error("评论失败");
        }
    }

    @PutMapping("/update")
    public Result updateComment(@RequestBody Comment comment) {
        boolean success = commentService.updateComment(comment);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        boolean success = commentService.deleteComment(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
