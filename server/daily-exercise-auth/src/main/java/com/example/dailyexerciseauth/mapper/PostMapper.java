package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {

    @Select("SELECT p.*, o.userName AS authorName FROM post p LEFT JOIN ordinary_user o ON p.authorID = o.userID WHERE p.auditState = 1 ORDER BY p.publishTime DESC")
    List<Post> getAllApprovedPosts();

    @Select("SELECT p.*, o.userName AS authorName FROM post p LEFT JOIN ordinary_user o ON p.authorID = o.userID WHERE p.authorID = #{authorID} ORDER BY p.publishTime DESC")
    List<Post> getPostsByAuthorId(Integer authorID);

    @Select("SELECT p.*, o.userName AS authorName FROM post p LEFT JOIN ordinary_user o ON p.authorID = o.userID WHERE p.postID = #{postID}")
    Post getPostById(Integer postID);

    @Insert("""
        INSERT INTO post (authorID, title, content, publishTime, auditState)
        VALUES (#{authorID}, #{title}, #{content}, #{publishTime}, #{auditState})
        """)
    int insertPost(Post post);

    @Update("""
        UPDATE post SET title = #{title}, content = #{content}, auditState = #{auditState}
        WHERE postID = #{postID}
        """)
    int updatePost(Post post);

    @Delete("DELETE FROM post WHERE postID = #{postID}")
    int deletePost(Integer postID);

    @Update("UPDATE post SET auditState = #{auditState} WHERE postID = #{postID}")
    int updateAuditState(@Param("postID") Integer postID, @Param("auditState") Integer auditState);

    @Select("SELECT * FROM post WHERE auditState = 0 ORDER BY publishTime DESC")
    List<Post> getPendingPosts();
}
