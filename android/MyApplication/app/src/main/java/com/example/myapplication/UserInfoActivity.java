package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfoActivity extends AppCompatActivity {

    private TextView tvUserId;
    private TextView tvUserName;
    private TextView tvPhone;
    private TextView tvEmail;
    private TextView tvGender;
    private TextView tvBirthday;
    private TextView tvAge;
    private TextView tvWeight;
    private TextView tvRegisterTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUserInfo();
    }

    private void initViews() {
        tvUserId = findViewById(R.id.tv_user_id_value);
        tvUserName = findViewById(R.id.tv_user_name_value);
        tvPhone = findViewById(R.id.tv_phone_value);
        tvEmail = findViewById(R.id.tv_email_value);
        tvGender = findViewById(R.id.tv_gender_value);
        tvBirthday = findViewById(R.id.tv_birthday_value);
        tvAge = findViewById(R.id.tv_age_value);
        tvWeight = findViewById(R.id.tv_weight_value);
        tvRegisterTime = findViewById(R.id.tv_register_time_value);

        View backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void loadUserInfo() {
        User user = getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<OrdinaryUser>> call = apiService.getUserInfo(user);

        call.enqueue(new Callback<Result<OrdinaryUser>>() {
            @Override
            public void onResponse(Call<Result<OrdinaryUser>> call, Response<Result<OrdinaryUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<OrdinaryUser> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        OrdinaryUser userData = result.getData();
                        displayUserInfo(userData);
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                Log.e("UserInfo", "加载用户信息失败", t);
            }
        });
    }

    private void displayUserInfo(OrdinaryUser user) {
        tvUserId.setText(user.getUserID() != null ? user.getUserID().toString() : "");
        tvUserName.setText(user.getUserName() != null ? user.getUserName() : "");
        tvPhone.setText(user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        tvEmail.setText(user.getUserMailbox() != null ? user.getUserMailbox() : "");
        tvGender.setText(user.getGender() != null ? user.getGender() : "");
        tvBirthday.setText(user.getBirthday() != null ? user.getBirthday() : "");
        tvAge.setText(user.getAge() != null ? user.getAge().toString() : "");
        tvWeight.setText(user.getWeight() != null ? user.getWeight() + " kg" : "");
        
        String registerTime = user.getRegisterTime() != null ? user.getRegisterTime() : "";
        tvRegisterTime.setText(formatDate(registerTime));
    }

    private String formatDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return "";
        }
        try {
            if (dateStr.contains("T")) {
                dateStr = dateStr.split("T")[0];
            }
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
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