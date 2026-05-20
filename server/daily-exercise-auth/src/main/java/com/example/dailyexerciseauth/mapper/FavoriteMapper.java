package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {

    @Select("SELECT * FROM favorite WHERE userID = #{userID} ORDER BY favoriteTime DESC")
    List<Favorite> getFavoritesByUserId(Integer userID);

    @Select("SELECT * FROM favorite WHERE userID = #{userID} AND targetType = #{targetType}")
    List<Favorite> getFavoritesByType(Integer userID, Integer targetType);

    @Select("SELECT * FROM favorite WHERE userID = #{userID} AND targetID = #{targetID} AND targetType = #{targetType}")
    Favorite getFavoriteByTarget(@Param("userID") Integer userID, @Param("targetID") Integer targetID, @Param("targetType") Integer targetType);

    @Insert("""
        INSERT INTO favorite (userID, targetID, targetType, linkUrl, favoriteTime)
        VALUES (#{userID}, #{targetID}, #{targetType}, #{linkUrl}, #{favoriteTime})
        """)
    int insertFavorite(Favorite favorite);

    @Delete("DELETE FROM favorite WHERE favoriteID = #{favoriteID}")
    int deleteFavorite(Integer favoriteID);

    @Delete("DELETE FROM favorite WHERE userID = #{userID} AND targetID = #{targetID} AND targetType = #{targetType}")
    int deleteFavoriteByTarget(@Param("userID") Integer userID, @Param("targetID") Integer targetID, @Param("targetType") Integer targetType);
}
