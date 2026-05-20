package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("SELECT c.*, o.userName FROM comment c LEFT JOIN ordinary_user o ON c.userID = o.userID WHERE c.postID = #{postID} ORDER BY c.publishTime ASC")
    List<Comment> getCommentsByPostId(Integer postID);

    @Select("SELECT * FROM comment WHERE CommentID = #{commentID}")
    Comment getCommentById(Integer commentID);

    @Insert("""
        INSERT INTO comment (postID, userID, content, publishTime)
        VALUES (#{postID}, #{userID}, #{content}, #{publishTime})
        """)
    int insertComment(Comment comment);

    @Update("UPDATE comment SET content = #{content} WHERE CommentID = #{commentID}")
    int updateComment(Comment comment);

    @Delete("DELETE FROM comment WHERE CommentID = #{commentID}")
    int deleteComment(Integer commentID);

    @Delete("DELETE FROM comment WHERE postID = #{postID}")
    int deleteCommentsByPostId(Integer postID);
}
