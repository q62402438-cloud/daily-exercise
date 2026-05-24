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

        String phoneNumber = user.getPhoneNumber();
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        phoneNumber = phoneNumber.trim();

        if (!isPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        System.out.println("=== Checking phone number: " + phoneNumber + " ===");
        
        User existingOrdinaryUser = userMapper.findUserByPhoneNumber(phoneNumber);
        System.out.println("=== Ordinary user check result: " + existingOrdinaryUser + " ===");
        
        User existingAdmin = userMapper.findAdminByPhoneNumber(phoneNumber);
        System.out.println("=== Admin check result: " + existingAdmin + " ===");
        
        if (existingOrdinaryUser != null || existingAdmin != null) {
            System.out.println("=== Phone number already exists in database! ===");
            throw new IllegalArgumentException("该手机号已被注册");
        }

        System.out.println("=== Phone number is available, proceeding with registration ===");

        user.setUserType(1);
        user.setPhoneNumber(phoneNumber);
        
        userMapper.insertUser(user);
        
        Integer userId = userMapper.getLastInsertId();
        
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

        String newUserName = user.getUserName();
        if (newUserName != null && !newUserName.isEmpty()) {
            int ordinaryUserCount = userMapper.countOrdinaryUserByUserName(newUserName, user.getUserID());
            int adminCount = userMapper.countAdminByUserName(newUserName, user.getUserID());
            
            if (ordinaryUserCount > 0 || adminCount > 0) {
                throw new IllegalArgumentException("该用户名已被使用");
            }
        }

        int userUpdated = 0;
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
        
        phoneNumber = phoneNumber.trim();
        
        User ordinaryUser = userMapper.findUserByPhoneNumber(phoneNumber);
        if (ordinaryUser != null) {
            return true;
        }
        
        User admin = userMapper.findAdminByPhoneNumber(phoneNumber);
        return admin != null;
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
        
        phoneNumber = phoneNumber.trim();
        
        User user = userMapper.findUserByPhoneNumber(phoneNumber);
        if (user == null) {
            user = userMapper.findAdminByPhoneNumber(phoneNumber);
        }
        
        if (user == null) {
            return false;
        }
        
        return userMapper.resetPassword(user.getUserID(), newPassword) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public int countOrdinaryUserByUserName(String userName, Integer excludeUserId) {
        if (userName == null || userName.isEmpty()) {
            return 0;
        }
        return userMapper.countOrdinaryUserByUserName(userName, excludeUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public int countAdminByUserName(String userName, Integer excludeUserId) {
        if (userName == null || userName.isEmpty()) {
            return 0;
        }
        return userMapper.countAdminByUserName(userName, excludeUserId);
    }

    @Override
    @Transactional
    public boolean deleteUser(Integer userID) {
        if (userID == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }

        userMapper.deleteFavoritesByUserId(userID);
        userMapper.deleteCommentsByUserId(userID);
        userMapper.deletePostsByAuthorId(userID);
        userMapper.deleteTrainingPlansByUserId(userID);
        userMapper.deleteExerciseRecordsByUserId(userID);
        userMapper.deleteOrdinaryUser(userID);
        userMapper.deleteUser(userID);
        
        return true;
    }
}
