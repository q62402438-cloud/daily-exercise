package com.example.dailyexerciseauth.service.impl;

import com.example.dailyexerciseauth.entity.OrdinaryUser;
import com.example.dailyexerciseauth.entity.User;
import com.example.dailyexerciseauth.mapper.UserMapper;
import com.example.dailyexerciseauth.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public User login(User user) {
        if (user == null) {
            return null;
        }
        
        String input = user.getUserName();
        if (input == null || input.isEmpty()) {
            input = user.getPhoneNumber();
        }
        
        if (isPhoneNumber(input)) {
            user.setPhoneNumber(input);
        }
        
        Integer userType = user.getUserType();
        
        if (userType == null || userType == 1) {
            return userMapper.loginOrdinaryUser(user);
        } else if (userType == 0) {
            return userMapper.loginAdmin(user);
        }
        
        return null;
    }
    
    private boolean isPhoneNumber(String input) {
        return input != null && input.matches("^1[3-9]\\d{9}$");
    }

    @Override
    @Transactional
    public boolean register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("用户信息不能为空");
        }

        // 设置用户类型：1-普通用户
        user.setUserType(1);
        
        // 1. 先插入 user 表（包含密码和用户类型）
        userMapper.insertUser(user);
        
        // 2. 获取刚插入的用户ID
        Integer userId = userMapper.getLastInsertId();
        
        // 3. 插入 ordinary_user 表（使用获取的 userID）
        OrdinaryUser ou = new OrdinaryUser();
        BeanUtils.copyProperties(user, ou);
        ou.setUserID(userId);
        ou.setRegisterTime(LocalDateTime.now());
        
        return userMapper.insertOrdinaryUser(ou) > 0;
    }

    @Override
    @Transactional
    public boolean update(OrdinaryUser user) {
        if (user == null || user.getUserID() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        int userUpdated = 0;
        // 只有当用户提供了新密码时才更新密码
        if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            userUpdated = userMapper.updateUserPassword(user);
        }
        
        int ordinaryUserUpdated = userMapper.updateOrdinaryUser(user);
        
        return userUpdated > 0 || ordinaryUserUpdated > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public OrdinaryUser getUserInfo(User user) {
        if (user == null || user.getUserID() == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        return userMapper.getOrdinaryUserByUserId(user.getUserID());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        User user = userMapper.findUserByPhoneNumber(phoneNumber);
        return user != null;
    }

    @Override
    @Transactional
    public boolean resetPassword(String phoneNumber, String newPassword) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("新密码不能为空");
        }
        
        User user = userMapper.findUserByPhoneNumber(phoneNumber);
        if (user == null) {
            return false;
        }
        
        return userMapper.resetPassword(user.getUserID(), newPassword) > 0;
    }
}
