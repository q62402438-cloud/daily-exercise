package com.example.dailyexerciseauth.common;

public class Result {
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

    public static Result error(int code, String msg) {
        Result r = new Result();
        r.code = code;
        r.msg = msg;
        return r;
    }
}
