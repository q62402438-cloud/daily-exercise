package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.OrdinaryUser;
import com.example.dailyexerciseauth.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 统一登录接口
    @Select("""
        SELECT u.*, 0 AS userType
        FROM ordinary_user o
        JOIN user u ON o.userID = u.userID
        WHERE (o.userName = #{userName} OR o.phoneNumber = #{phoneNumber})
          AND u.userPassword = #{userPassword}
        """)
    User login(User user);

    // 插入用户基础信息
    @Insert("INSERT INTO user (userPassword, userType) VALUES (#{userPassword}, #{userType})")
    int insertUser(User user);

    // 获取刚插入的用户ID
    @Select("SELECT LAST_INSERT_ID()")
    Integer getLastInsertId();

    // 插入普通用户详细信息
    @Insert("""
        INSERT INTO ordinary_user
        (userID, userName, phoneNumber, userMailbox, gender, birthday, registerTime, age, weight)
        VALUES
        (#{userID}, #{userName}, #{phoneNumber}, #{userMailbox}, #{gender}, #{birthday}, #{registerTime}, #{age}, #{weight})
        """)
    int insertOrdinaryUser(OrdinaryUser user);

    // 更新用户密码
    @Update("UPDATE user SET userPassword = #{userPassword} WHERE userID = #{userID}")
    int updateUserPassword(User user);

    // ✅ 修正后的普通用户更新方法
    @Update("""
        UPDATE ordinary_user
        SET userName = #{userName},
            phoneNumber = #{phoneNumber},
            userMailbox = #{userMailbox},
            gender = #{gender},
            birthday = #{birthday},
            age = #{age},
            weight = #{weight}
        WHERE userID = #{userID}
        """)
    int updateOrdinaryUser(OrdinaryUser user);

    @Select("""
        SELECT o.userID, o.userName, o.phoneNumber, o.userMailbox, o.gender, 
               o.birthday, o.registerTime, o.age, o.weight, u.userPassword, u.userType
        FROM ordinary_user o
        JOIN user u ON o.userID = u.userID
        WHERE o.userID = #{userID}
        """)
    OrdinaryUser getOrdinaryUserByUserId(Integer userID);

    @Select("SELECT u.userID, u.userPassword, u.userType FROM user u JOIN ordinary_user o ON u.userID = o.userID WHERE o.phoneNumber = #{phoneNumber}")
    User findUserByPhoneNumber(String phoneNumber);

    @Update("UPDATE user SET userPassword = #{newPassword} WHERE userID = #{userID}")
    int resetPassword(@Param("userID") Integer userID, @Param("newPassword") String newPassword);
}