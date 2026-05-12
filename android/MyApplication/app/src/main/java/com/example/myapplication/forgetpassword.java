package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.User;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class forgetpassword extends AppCompatActivity {

    private EditText etPhone, etVerifyCode, etNewPwd, etConfirmPwd;
    private Button btnSendCode, btnReset, btnBack;

    private CountDownTimer countDownTimer;
    private boolean codeSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etPhone = findViewById(R.id.usernameInput);
        etVerifyCode = findViewById(R.id.yanzhengmaInput);
        etNewPwd = findViewById(R.id.passwordInput);
        etConfirmPwd = findViewById(R.id.confirmPasswordInput);

        btnSendCode = findViewById(R.id.identity_text);
        btnReset = findViewById(R.id.registerButton);
        btnBack = findViewById(R.id.backButton);

        etVerifyCode.setEnabled(false);
        etNewPwd.setEnabled(false);
        etConfirmPwd.setEnabled(false);
        btnReset.setEnabled(false);
    }

    private void setupListeners() {
        btnSendCode.setOnClickListener(v -> sendVerifyCode());
        btnReset.setOnClickListener(v -> resetPassword());
        btnBack.setOnClickListener(v -> finish());
    }

    private void sendVerifyCode() {
        String phone = etPhone.getText().toString().trim();

        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            showToast("请输入11位手机号码");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        User user = new User();
        user.setPhoneNumber(phone);

        Call<Result<Object>> call = apiService.sendCode(user);
        call.enqueue(new Callback<Result<Object>>() {
            @Override
            public void onResponse(Call<Result<Object>> call, Response<Result<Object>> response) {
                Log.d("ForgetPassword", "HTTP状态码: " + response.code());
                Log.d("ForgetPassword", "响应是否成功: " + response.isSuccessful());
                
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Result<Object> result = response.body();
                        Log.d("ForgetPassword", "业务码: " + result.getCode());
                        Log.d("ForgetPassword", "消息: " + result.getMessage());
                        
                        if (result.getCode() == 200) {
                            showToast("验证码已发送，请查看服务端控制台");
                            codeSent = true;
                            etVerifyCode.setEnabled(true);
                            etNewPwd.setEnabled(true);
                            etConfirmPwd.setEnabled(true);
                            btnReset.setEnabled(true);
                            btnSendCode.setEnabled(false);
                            startCountDown();
                        } else {
                            showToast("发送失败: " + result.getMessage());
                        }
                    } else {
                        showToast("响应体为空");
                        Log.e("ForgetPassword", "响应体为空");
                    }
                } else {
                    showToast("发送失败，状态码：" + response.code());
                    Log.e("ForgetPassword", "HTTP失败，状态码: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Result<Object>> call, Throwable t) {
                showToast("网络请求失败：" + t.getMessage());
                Log.e("ForgetPassword", "网络请求失败", t);
            }
        });
    }

    private void startCountDown() {
        btnSendCode.setText("60秒后重发");

        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnSendCode.setText(millisUntilFinished / 1000 + "秒后重发");
            }

            @Override
            public void onFinish() {
                btnSendCode.setEnabled(true);
                btnSendCode.setText("发送验证码");
            }
        }.start();
    }

    private void resetPassword() {
        String phone = etPhone.getText().toString().trim();
        String verifyCode = etVerifyCode.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();
        String confirmPwd = etConfirmPwd.getText().toString().trim();

        if (!codeSent) {
            showToast("请先点击发送验证码");
            return;
        }
        if (TextUtils.isEmpty(verifyCode)) {
            showToast("请输入收到的验证码");
            return;
        }
        if (TextUtils.isEmpty(newPwd) || newPwd.length() < 6) {
            showToast("新密码至少需要6位");
            return;
        }
        if (!newPwd.equals(confirmPwd)) {
            showToast("两次输入的密码不一致");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        User user = new User();
        user.setPhoneNumber(phone);
        user.setVerifyCode(verifyCode);
        user.setUserPassword(newPwd);

        Call<Result<String>> call = apiService.resetPassword(user);
        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        showToast("密码重置成功，即将返回登录页");
                        Intent intent = new Intent(forgetpassword.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        showToast("重置失败: " + (result.getMessage() != null ? result.getMessage() : "未知错误"));
                    }
                } else {
                    showToast("重置失败，服务器返回: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                showToast("网络连接失败，请检查网络后重试");
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}