package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.User;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class register extends AppCompatActivity {

    private EditText etPhone, etPwd, etConfirmPwd;
    private Button btnRegister;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.usernameInput);
        etPwd = findViewById(R.id.passwordInput);
        etConfirmPwd = findViewById(R.id.confirmPasswordInput);

        btnRegister = findViewById(R.id.registerButton);
        tvBackToLogin = findViewById(R.id.tv_backtoLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Register", "注册按钮被点击");
                handleRegister();
            }
        });

        tvBackToLogin.setOnClickListener(v -> finish());
    }

    /**
     * ✅ 真正调用后端注册接口
     */
    private void handleRegister() {
        Log.d("Register", "进入handleRegister方法");
        
        String phone = etPhone.getText().toString().trim();
        String pwd = etPwd.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();

        Log.d("Register", "phone: " + phone + ", pwd: " + pwd + ", confirmPwd: " + confirmPwd);

        if (TextUtils.isEmpty(phone)) {
            Log.d("Register", "手机号为空");
            showToast("请输入手机号");
            etPhone.requestFocus();
            return;
        }
        if (phone.length() != 11) {
            Log.d("Register", "手机号格式错误");
            showToast("手机号必须是11位");
            etPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            Log.d("Register", "密码为空");
            showToast("请输入密码");
            etPwd.requestFocus();
            return;
        }
        if (pwd.length() < 6) {
            Log.d("Register", "密码太短");
            showToast("密码至少6位");
            etPwd.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(confirmPwd)) {
            Log.d("Register", "确认密码为空");
            showToast("请确认密码");
            etConfirmPwd.requestFocus();
            return;
        }
        if (!pwd.equals(confirmPwd)) {
            Log.d("Register", "两次密码不一致");
            showToast("两次密码输入不一致");
            etConfirmPwd.requestFocus();
            return;
        }

        Log.d("Register", "输入验证通过，准备创建用户对象");

        User newUser = new User();
        newUser.setUserName(phone);
        newUser.setUserPassword(pwd);
        newUser.setPhoneNumber(phone);
        newUser.setUserType(1); // 1-普通用户

        Log.d("Register", "准备创建ApiService");
        
        try {
            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Log.d("Register", "ApiService创建成功");

            Call<Result<String>> call = apiService.register(newUser);
            Log.d("Register", "准备发送注册请求");

            call.enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    Log.d("Register", "收到响应，code: " + response.code());
                    
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            Result<String> result = response.body();
                            Log.d("Register", "响应码: " + result.getCode() + ", 消息: " + result.getMessage());
                            
                            if (result.getCode() == 200) {
                                showToast("注册成功！请返回登录");
                                finish();
                            } else {
                                showToast("注册失败：" + result.getMessage());
                            }
                        } else {
                            Log.d("Register", "响应体为空");
                            showToast("响应体为空");
                        }
                    } else {
                        Log.d("Register", "HTTP错误: " + response.code());
                        String errorMsg = "注册失败";
                        if (response.code() == 500) {
                            errorMsg = "服务器内部错误，请稍后重试";
                        } else if (response.code() == 404) {
                            errorMsg = "注册接口未找到";
                        } else if (response.code() == 400) {
                            errorMsg = "请求参数错误";
                        }
                        showToast(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Log.e("Register", "网络请求失败", t);
                    showToast("网络请求失败：" + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("Register", "注册过程中发生异常", e);
            showToast("注册异常：" + e.getMessage());
        }
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}