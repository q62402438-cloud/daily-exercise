package com.example.dailyexerciseauth.controller;

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