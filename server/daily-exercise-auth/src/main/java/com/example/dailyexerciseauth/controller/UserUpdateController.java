package com.example.dailyexerciseauth.controller;

import com.example.dailyexerciseauth.common.Result;
import com.example.dailyexerciseauth.entity.OrdinaryUser;
import com.example.dailyexerciseauth.entity.User;
import com.example.dailyexerciseauth.service.SmsService;
import com.example.dailyexerciseauth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserUpdateController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private SmsService smsService;

    @PostMapping("/update")
    public Result update(@RequestBody OrdinaryUser user) {
        System.out.println("=== Update Request ===");
        System.out.println("User object: " + user);
        System.out.println("userID: " + user.getUserID());
        
        try {
            boolean success = userService.update(user);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/checkUserName")
    public Result checkUserName(@RequestBody Map<String, Object> request) {
        String userName = (String) request.get("userName");
        Integer excludeUserId = (Integer) request.get("excludeUserId");
        
        System.out.println("=== Check UserName Request ===");
        System.out.println("userName: " + userName);
        System.out.println("excludeUserId: " + excludeUserId);
        
        int ordinaryUserCount = userService.countOrdinaryUserByUserName(userName, excludeUserId);
        int adminCount = userService.countAdminByUserName(userName, excludeUserId);
        
        boolean exists = ordinaryUserCount > 0 || adminCount > 0;
        
        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("message", exists ? "用户名已存在" : "用户名可用");
        
        return Result.success(result);
    }

    @PostMapping("/sendCode")
    public Result sendCode(@RequestBody User user) {
        System.out.println("=== Send Code Request ===");
        System.out.println("phoneNumber: " + user.getPhoneNumber());
        
        boolean exists = userService.verifyPhoneNumber(user.getPhoneNumber());
        if (!exists) {
            return Result.error("手机号未注册");
        }
        
        String code = smsService.sendCode(user.getPhoneNumber());
        
        Map<String, Object> result = new HashMap<>();
        result.put("phoneNumber", user.getPhoneNumber());
        result.put("message", "验证码已发送，请注意查收");
        
        return Result.success(result);
    }

    @PostMapping("/verifyCode")
    public Result verifyCode(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String code = request.get("code");
        
        System.out.println("=== Verify Code Request ===");
        System.out.println("phoneNumber: " + phoneNumber);
        System.out.println("code: " + code);
        
        boolean isValid = smsService.verifyCode(phoneNumber, code);
        if (isValid) {
            return Result.success("验证码验证成功");
        } else {
            return Result.error("验证码无效或已过期");
        }
    }

    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String newPassword = request.get("userPassword");
        String verifyCode = request.get("verifyCode");
        
        System.out.println("=== Reset Password Request ===");
        System.out.println("phoneNumber: " + phoneNumber);
        System.out.println("verifyCode: " + verifyCode);
        
        if (!smsService.verifyCode(phoneNumber, verifyCode)) {
            return Result.error("验证码无效或已过期");
        }
        
        boolean success = userService.resetPassword(phoneNumber, newPassword);
        if (success) {
            return Result.success("密码重置成功");
        } else {
            return Result.error("密码重置失败，手机号未注册");
        }
    }
}
