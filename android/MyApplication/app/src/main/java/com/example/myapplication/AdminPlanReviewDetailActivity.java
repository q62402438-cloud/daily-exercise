package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPlanReviewDetailActivity extends AppCompatActivity {

    private Integer planId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_plan_review_detail);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        loadPlanDetails();
        setupClickListeners();
    }

    private void loadPlanDetails() {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvSportType = findViewById(R.id.tv_sport_type);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        TextView tvDailyExercise = findViewById(R.id.tv_daily_exercise);
        TextView tvUserName = findViewById(R.id.tv_user_name);
        TextView tvPlanDetail = findViewById(R.id.tv_plan_detail);
        TextView tvStatus = findViewById(R.id.tv_status);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            planId = bundle.getInt("plan_id", -1);
            String planName = bundle.getString("plan_name", "初级跑步计划");
            String sportType = bundle.getString("sport_type", "跑步");
            String startDate = bundle.getString("start_date", "2025-01-01");
            String endDate = bundle.getString("end_date", "2025-01-30");
            String dailyExercise = bundle.getString("daily_exercise", "5");
            Integer userId = (Integer) bundle.get("user_id");
            String planDetail = bundle.getString("plan_detail", "");

            if (tvPlanName != null) tvPlanName.setText(planName);
            if (tvSportType != null) tvSportType.setText(sportType);
            if (tvStartDate != null) tvStartDate.setText(startDate);
            if (tvEndDate != null) tvEndDate.setText(endDate);
            if (tvDailyExercise != null) tvDailyExercise.setText(dailyExercise);
            if (tvPlanDetail != null) tvPlanDetail.setText(planDetail != null && !planDetail.isEmpty() ? planDetail : "暂无详情");
            if (tvStatus != null) {
                tvStatus.setText("待审核");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }

            if (userId != null) {
                fetchUserName(userId, tvUserName);
            }
        }
    }

    private void fetchUserName(Integer userId, TextView tvUserName) {
        User user = new User();
        user.setUserID(userId);

        apiService.getUserInfo(user).enqueue(new Callback<Result<OrdinaryUser>>() {
            @Override
            public void onResponse(Call<Result<OrdinaryUser>> call, Response<Result<OrdinaryUser>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    String userName = response.body().getData().getUserName();
                    if (tvUserName != null) {
                        tvUserName.setText(userName != null ? userName : "未知用户");
                    }
                } else if (tvUserName != null) {
                    tvUserName.setText("未知用户");
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                if (tvUserName != null) {
                    tvUserName.setText("未知用户");
                }
            }
        });
    }

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button approveBtn = findViewById(R.id.btn_approve);
        if (approveBtn != null) {
            approveBtn.setOnClickListener(v -> {
                if (planId != null && planId != -1) {
                    approvePlan();
                } else {
                    Toast.makeText(AdminPlanReviewDetailActivity.this, "计划ID无效", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button rejectBtn = findViewById(R.id.btn_reject);
        if (rejectBtn != null) {
            rejectBtn.setOnClickListener(v -> {
                if (planId != null && planId != -1) {
                    rejectPlan();
                } else {
                    Toast.makeText(AdminPlanReviewDetailActivity.this, "计划ID无效", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void approvePlan() {
        Map<String, Integer> request = new HashMap<>();
        request.put("planID", planId);

        Call<Result<String>> call = apiService.auditPlanPass(request);
        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(AdminPlanReviewDetailActivity.this, "审核通过", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        String message = result.getMessage() != null ? result.getMessage() : "审核失败";
                        Toast.makeText(AdminPlanReviewDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminPlanReviewDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(AdminPlanReviewDetailActivity.this, "审核失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rejectPlan() {
        Map<String, Integer> request = new HashMap<>();
        request.put("planID", planId);

        Call<Result<String>> call = apiService.auditPlanReject(request);
        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(AdminPlanReviewDetailActivity.this, "审核不通过", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        String message = result.getMessage() != null ? result.getMessage() : "操作失败";
                        Toast.makeText(AdminPlanReviewDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminPlanReviewDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(AdminPlanReviewDetailActivity.this, "操作失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}