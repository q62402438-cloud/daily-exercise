package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CancelUserActivity extends AppCompatActivity {

    private Button btnCancel;
    private Button btnConfirm;
    private TextView tvWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cancel_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        tvWarning = findViewById(R.id.tv_warning);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);

        View backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        btnCancel.setOnClickListener(v -> goBack());
        btnConfirm.setOnClickListener(v -> showConfirmDialog());
    }

    private void goBack() {
        finish();
        overridePendingTransition(0, 0);
    }

    private void showConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认注销")
                .setMessage("确定要永久注销账户吗？此操作不可恢复！")
                .setPositiveButton("确认注销", (dialog, which) -> cancelAccount())
                .setNegativeButton("取消", null)
                .show();
    }

    private void cancelAccount() {
        User user = getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<Object>> call = apiService.cancelUser(user);

        call.enqueue(new Callback<Result<Object>>() {
            @Override
            public void onResponse(Call<Result<Object>> call, Response<Result<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<Object> result = response.body();
                    if (result.getCode() == 200) {
                        new SessionManager(CancelUserActivity.this).clear();
                        Toast.makeText(CancelUserActivity.this, "账户已成功注销", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CancelUserActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CancelUserActivity.this, result.getMessage() != null ? result.getMessage() : "注销失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CancelUserActivity.this, "注销失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<Object>> call, Throwable t) {
                Log.e("CancelUser", "注销失败", t);
                Toast.makeText(CancelUserActivity.this, "注销失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private User getCurrentUser() {
        SessionManager sessionManager = new SessionManager(this);
        Integer userId = sessionManager.getUserId();
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setUserID(userId);
        return user;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}