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

import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgetPassword;
    private CheckBox cbAdminLogin;
    private ApiService apiService;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
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
        cbAdminLogin = findViewById(R.id.cb_agree);
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

                boolean isAdmin = cbAdminLogin.isChecked();
                performLogin(username, password, isAdmin);
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

    private void performLogin(String username, String password, boolean isAdmin) {
        int userType = isAdmin ? 0 : 1;
        String role = isAdmin ? "管理员" : "普通用户";
        
        Log.d("Login", "========== 登录请求开始 ==========");
        Log.d("Login", "用户名: " + username);
        Log.d("Login", "用户类型: " + role + " (userType=" + userType + ")");
        Log.d("Login", "是否管理员: " + isAdmin);

        String requestJson = "{\"userName\":\"" + username + "\",\"phoneNumber\":\"" + username + "\",\"userPassword\":\"" + password + "\",\"userType\":" + userType + "}";
        Log.d("Login", "请求体(JSON): " + requestJson);

        okhttp3.RequestBody body = okhttp3.RequestBody.create(requestJson, okhttp3.MediaType.parse("application/json; charset=utf-8"));

        Call<Result<User>> call = apiService.login(body);
        call.enqueue(new Callback<Result<User>>() {
            @Override
            public void onResponse(Call<Result<User>> call, Response<Result<User>> response) {
                Log.d("Login", "响应码: " + response.code());
                Log.d("Login", "响应是否成功: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    Result<User> result = response.body();
                    Log.d("Login", "接口返回code: " + result.getCode());
                    Log.d("Login", "接口返回message: " + result.getMessage());

                    if (result.getCode() == 200) {
                        Log.d("Login", "登录成功！");
                        Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        
                        Intent intent = new Intent(MainActivity.this, ChangeUserInfo.class);
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
                        Log.d("Login", "登录失败: " + message);
                        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Login", "登录失败: 响应为空或失败");
                    Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                }
                Log.d("Login", "========== 登录请求结束 ==========");
            }

            @Override
            public void onFailure(Call<Result<User>> call, Throwable t) {
                Log.e("Login", "网络请求失败", t);
                Toast.makeText(MainActivity.this, "网络请求失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Login", "========== 登录请求结束 ==========");
            }
        });
    }
}