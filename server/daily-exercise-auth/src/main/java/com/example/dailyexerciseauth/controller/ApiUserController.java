package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.entity.OrdinaryUser;
import com.example.dailyexerciseauth.entity.User;
import com.example.dailyexerciseauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class ApiUserController {

    @Autowired
    private UserService userService;

    @PostMapping("/getUserInfo")
    public Result getUserInfo(@RequestBody User user) {
        System.out.println("=== GetUserInfo API Request ===");
        System.out.println("userID: " + user.getUserID());
        
        OrdinaryUser ordinaryUser = userService.getUserInfo(user);
        if (ordinaryUser != null) {
            return Result.success(ordinaryUser);
        } else {
            return Result.error("用户不存在");
        }
    }
}
