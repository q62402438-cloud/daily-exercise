package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgetPassword;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
        apiService = RetrofitClient.getInstance().create(ApiService.class);
    }

    private void initView() {
        etUsername = findViewById(R.id.account_et);
        etPassword = findViewById(R.id.password_et);
        btnLogin = findViewById(R.id.login_button);
        tvRegister = findViewById(R.id.tv_register);
        tvForgetPassword = findViewById(R.id.tv_forget_password);
    }

    private void initListener() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                login(username, password);
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, register.class);
                startActivity(intent);
            }
        });

        tvForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, forgetpassword.class);
                startActivity(intent);
            }
        });
    }

    private void login(String username, String password) {
        // 创建只包含必要字段的请求对象
        User user = new User();
        user.setUserName(username);
        user.setPhoneNumber(username);
        user.setUserPassword(password);
        // 不设置 userID 和 userType，避免发送 null 值
        
        Call<Result<User>> call = apiService.login(user);
        call.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<User> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, ChangeUserInfo.class);
                        // 将 User 对象转换为 OrdinaryUser 对象
                        if (result.getData() != null) {
                            User user = result.getData();
                            OrdinaryUser ordinaryUser = new OrdinaryUser();
                            ordinaryUser.setUserID(user.getUserID());
                            ordinaryUser.setUserName(user.getUserName());
                            ordinaryUser.setUserPassword(user.getUserPassword());
                            // 如果后端返回的 phoneNumber 为空，使用登录时输入的用户名作为电话号码
                            String phoneNumber = user.getPhoneNumber();
                            if (phoneNumber == null || phoneNumber.isEmpty()) {
                                phoneNumber = username;
                            }
                            ordinaryUser.setPhoneNumber(phoneNumber);
                            ordinaryUser.setUserType(user.getUserType());
                            intent.putExtra("user_data", ordinaryUser);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        String message = result.getMessage();
                        if (message == null || message.isEmpty()) {
                            message = "登录失败";
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e("MainActivity", "Login failed", t);
                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
