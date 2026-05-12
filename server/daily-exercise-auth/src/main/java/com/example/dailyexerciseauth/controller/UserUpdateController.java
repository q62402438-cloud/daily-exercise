package com.example.dailyexerciseauth.controller;

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
        
        boolean success = userService.update(user);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
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

    static class Result {
        private int code;
        private String msg;
        private Object data;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public static Result success(Object data) {
            Result r = new Result();
            r.code = 200;
            r.msg = "success";
            r.data = data;
            return r;
        }

        public static Result success(String msg) {
            Result r = new Result();
            r.code = 200;
            r.msg = msg;
            return r;
        }

        public static Result error(String msg) {
            Result r = new Result();
            r.code = 500;
            r.msg = msg;
            return r;
        }
    }
}
