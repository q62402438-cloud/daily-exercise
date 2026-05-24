package com.example.dailyexerciseauth.mapper;

import com.example.dailyexerciseauth.entity.Administrator;
import com.example.dailyexerciseauth.entity.OrdinaryUser;
import com.example.dailyexerciseauth.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    // 普通用户登录 - 支持用户名或手机号登录
    @Select("""
        SELECT u.userID, u.userPassword, u.userType, o.userName, o.phoneNumber
        FROM ordinary_user o
        JOIN user u ON o.userID = u.userID
        WHERE (o.userName = #{userName} OR o.phoneNumber = #{userName} OR o.phoneNumber = #{phoneNumber})
          AND u.userPassword = #{userPassword}
          AND u.userType = 1
        """)
    User loginOrdinaryUser(User user);

    // 管理员登录 - 支持用户名或手机号登录
    @Select("""
        SELECT u.userID, u.userPassword, u.userType, a.userName, a.phoneNumber
        FROM administrator a
        JOIN user u ON a.userID = u.userID
        WHERE (a.userName = #{userName} OR a.phoneNumber = #{userName} OR a.phoneNumber = #{phoneNumber})
          AND u.userPassword = #{userPassword}
          AND u.userType = 0
        """)
    User loginAdmin(User user);

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

    @Select("""
        SELECT u.userID, u.userPassword, u.userType, 
               o.userName as userName, 
               o.phoneNumber as phoneNumber
        FROM user u 
        INNER JOIN ordinary_user o ON u.userID = o.userID
        WHERE o.phoneNumber = #{phoneNumber}
        """)
    User findUserByPhoneNumber(String phoneNumber);

    @Select("""
        SELECT u.userID, u.userPassword, u.userType, 
               a.userName as userName, 
               a.phoneNumber as phoneNumber
        FROM user u 
        INNER JOIN administrator a ON u.userID = a.userID
        WHERE a.phoneNumber = #{phoneNumber}
        """)
    User findAdminByPhoneNumber(String phoneNumber);

    @Update("UPDATE user SET userPassword = #{newPassword} WHERE userID = #{userID}")
    int resetPassword(@Param("userID") Integer userID, @Param("newPassword") String newPassword);

    @Select("""
        SELECT COUNT(*) FROM ordinary_user WHERE userName = #{userName} AND userID != #{excludeUserId}
        """)
    int countOrdinaryUserByUserName(@Param("userName") String userName, @Param("excludeUserId") Integer excludeUserId);

    @Select("""
        SELECT COUNT(*) FROM administrator WHERE userName = #{userName} AND userID != #{excludeUserId}
        """)
    int countAdminByUserName(@Param("userName") String userName, @Param("excludeUserId") Integer excludeUserId);
}