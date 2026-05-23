package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConsoleActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvTodayPlans;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_console);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadUserInfo();
        loadTodayPlans();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvTodayPlans = findViewById(R.id.tv_today_plans);
        tvWelcome = findViewById(R.id.tv_welcome);

        View backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout viewProfile = findViewById(R.id.card_view_profile);
        if (viewProfile != null) {
            viewProfile.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, UserInfoActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout editProfile = findViewById(R.id.card_edit_profile);
        if (editProfile != null) {
            editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, ChangeUserInfo.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout trainingPlan = findViewById(R.id.card_training_plan);
        if (trainingPlan != null) {
            trainingPlan.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, CreateExercisePlanActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout forum = findViewById(R.id.card_forum);
        if (forum != null) {
            forum.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, ForumActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout checkin = findViewById(R.id.card_checkin);
        if (checkin != null) {
            checkin.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, CheckInRecordsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout favorites = findViewById(R.id.card_favorites);
        if (favorites != null) {
            favorites.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, FavoritePostsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        Button checkinBtn = findViewById(R.id.btn_checkin);
        if (checkinBtn != null) {
            checkinBtn.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, ExerciseCheckInActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout changePassword = findViewById(R.id.card_change_password);
        if (changePassword != null) {
            changePassword.setOnClickListener(v -> {
                Intent intent = new Intent(UserConsoleActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout logout = findViewById(R.id.card_logout);
        if (logout != null) {
            logout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("退出登录")
                .setMessage("请选择操作")
                .setPositiveButton("退出登录", (dialog, which) -> logout())
                .setNegativeButton("注销账户", (dialog, which) -> {
                    Intent intent = new Intent(UserConsoleActivity.this, CancelUserActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                })
                .setNeutralButton("取消", null)
                .show();
    }

    private void logout() {
        new SessionManager(this).clear();
        Intent intent = new Intent(UserConsoleActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
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
                        String userName = userData.getUserName() != null ? userData.getUserName() : "用户";
                        tvUserName.setText("欢迎，" + userName);
                        tvWelcome.setText("管理您的个人资料和运动计划");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                Log.e("UserConsole", "加载用户信息失败", t);
                tvUserName.setText("欢迎，用户");
            }
        });
    }

    private void loadTodayPlans() {
        User user = getCurrentUser();
        if (user == null) {
            tvTodayPlans.setText("暂无今日计划");
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<TrainingPlan>>> call = apiService.getTrainingPlansByUser(user);

        call.enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<TrainingPlan>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        List<TrainingPlan> plans = result.getData();
                        if (plans.isEmpty()) {
                            tvTodayPlans.setText("暂无今日计划");
                        } else {
                            StringBuilder sb = new StringBuilder();
                            for (TrainingPlan plan : plans) {
                                if (sb.length() > 0) sb.append("\n");
                                sb.append("• ").append(plan.getPlanName() != null ? plan.getPlanName() : "未命名计划");
                            }
                            tvTodayPlans.setText(sb.toString());
                        }
                    } else {
                        tvTodayPlans.setText("暂无今日计划");
                    }
                } else {
                    tvTodayPlans.setText("暂无今日计划");
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                Log.e("UserConsole", "加载计划失败", t);
                tvTodayPlans.setText("暂无今日计划");
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