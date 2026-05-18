package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.model.AdminUser;
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgetPassword;
    private CheckBox cbAdminLogin, cbAgreePrivacy;
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
        cbAdminLogin = findViewById(R.id.identity);
        cbAgreePrivacy = findViewById(R.id.cb_agree);
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

                boolean isAdminLogin = cbAdminLogin.isChecked();
                if (isAdminLogin) {
                    adminLogin(username, password);
                } else {
                    userLogin(username, password);
                }
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

    private void userLogin(String username, String password) {
        User user = new User();
        user.setUserName(username);
        user.setPhoneNumber(username);
        user.setUserPassword(password);

        Call<Result<User>> call = apiService.login(user);
        call.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<User> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                        if (result.getData() != null) {
                            User user = result.getData();
                            OrdinaryUser ordinaryUser = new OrdinaryUser();
                            ordinaryUser.setUserID(user.getUserID());
                            ordinaryUser.setUserName(user.getUserName());
                            ordinaryUser.setUserPassword(user.getUserPassword());
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
                Log.e("MainActivity", "User Login failed", t);
                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void adminLogin(String username, String password) {
        User user = new User();
        user.setUserName(username);
        user.setUserPassword(password);
        user.setUserType(0);  // ✅ 添加 userType = 0 表示管理员

        Log.d("MainActivity", "=== 管理员登录请求 ===");
        Log.d("MainActivity", "username: " + username);
        Log.d("MainActivity", "password: " + password);
        Log.d("MainActivity", "userType: " + user.getUserType());

        // ✅ 使用统一的登录接口 api/auth/login
        Call<Result<User>> call = apiService.login(user);
        call.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                Log.d("MainActivity", "管理员登录响应:");
                Log.d("MainActivity", "response.isSuccessful(): " + response.isSuccessful());
                Log.d("MainActivity", "response.code(): " + response.code());
                Log.d("MainActivity", "response.message(): " + response.message());
                
                if (response.isSuccessful() && response.body() != null) {
                    Result<User> result = response.body();
                    Log.d("MainActivity", "result.getCode(): " + result.getCode());
                    Log.d("MainActivity", "result.getMessage(): " + result.getMessage());
                    
                    if (result.getCode() == 200) {
                        Toast.makeText(MainActivity.this, "管理员登录成功", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(MainActivity.this, AdminPage.class);
                        if (result.getData() != null) {
                            User admin = result.getData();
                            AdminUser adminUser = new AdminUser();
                            adminUser.setAdminId(admin.getPhoneNumber());
                            adminUser.setAdminName(admin.getUserName());
                            adminUser.setRole("系统管理员");
                            intent.putExtra("admin_data", adminUser);
                        } else {
                            AdminUser defaultAdmin = new AdminUser();
                            defaultAdmin.setAdminId(username);
                            defaultAdmin.setAdminName("系统管理员");
                            defaultAdmin.setRole("系统管理员");
                            intent.putExtra("admin_data", defaultAdmin);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        String message = result.getMessage();
                        if (message == null || message.isEmpty()) {
                            message = "管理员登录失败";
                        }
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("MainActivity", "管理员登录失败 - 响应不成功或body为空");
                    Toast.makeText(MainActivity.this, "管理员登录失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e("MainActivity", "Admin Login failed", t);
                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}