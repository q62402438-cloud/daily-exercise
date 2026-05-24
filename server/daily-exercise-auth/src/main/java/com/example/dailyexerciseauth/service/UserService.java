package com.example.dailyexerciseauth.service;

import com.example.dailyexerciseauth.entity.OrdinaryUser;
import com.example.dailyexerciseauth.entity.User;

public interface UserService {
    User login(User user);
    boolean register(User user);
    boolean update(OrdinaryUser user);
    OrdinaryUser getUserInfo(User user);
    boolean verifyPhoneNumber(String phoneNumber);
    boolean resetPassword(String phoneNumber, String newPassword);
    int countOrdinaryUserByUserName(String userName, Integer excludeUserId);
    int countAdminByUserName(String userName, Integer excludeUserId);
}