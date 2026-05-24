package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class Result<T> {
    private int code;
    
    @SerializedName("msg")
    private String message;
    
    private T data;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}