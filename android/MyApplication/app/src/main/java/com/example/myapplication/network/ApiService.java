package com.example.myapplication.network;

import com.example.myapplication.model.AdminUser;
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("api/auth/login")
    Call<Result<User>> login(@Body User user);

    @POST("api/auth/admin/login")
    Call<Result<User>> adminLogin(@Body User user);

    @POST("api/auth/register")
    Call<Result<String>> register(@Body User user);

    @POST("api/user/update")
    Call<Result<String>> updateUser(@Body OrdinaryUser user);

    @POST("api/user/getUserInfo")
    Call<Result<OrdinaryUser>> getUserInfo(@Body User user);

    @POST("api/user/sendCode")
    Call<Result<Object>> sendCode(@Body User user);

    @POST("api/auth/resetPassword")
    Call<Result<String>> resetPassword(@Body User user);

    @POST("api/auth/forgetPassword")
    Call<Result<String>> forgetPassword(@Body User user);
}