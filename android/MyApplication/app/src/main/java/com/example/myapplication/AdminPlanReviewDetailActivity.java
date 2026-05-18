package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPlanReviewDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_plan_review_detail);

        loadPlanDetails();
        setupClickListeners();
    }

    private void loadPlanDetails() {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvSportType = findViewById(R.id.tv_sport_type);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        TextView tvDailyExercise = findViewById(R.id.tv_daily_exercise);
        TextView tvDailyCalorie = findViewById(R.id.tv_daily_calorie);
        TextView tvStatus = findViewById(R.id.tv_status);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            String planName = bundle.getString("plan_name", "初级跑步计划");
            String sportType = bundle.getString("sport_type", "跑步");
            String startDate = bundle.getString("start_date", "2025-01-01");
            String endDate = bundle.getString("end_date", "2025-01-30");
            String dailyExercise = bundle.getString("daily_exercise", "5");
            String dailyCalorie = bundle.getString("daily_calorie", "300");
            boolean isPublic = bundle.getBoolean("is_public", false);

            if (tvPlanName != null) tvPlanName.setText(planName);
            if (tvSportType != null) tvSportType.setText(sportType);
            if (tvStartDate != null) tvStartDate.setText(startDate);
            if (tvEndDate != null) tvEndDate.setText(endDate);
            if (tvDailyExercise != null) tvDailyExercise.setText(dailyExercise + " 公里");
            if (tvDailyCalorie != null) tvDailyCalorie.setText(dailyCalorie + " kcal");
            if (tvStatus != null) {
                tvStatus.setText("待审核");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
        }
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
                Toast.makeText(AdminPlanReviewDetailActivity.this,
                        "审核通过",
                        Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button rejectBtn = findViewById(R.id.btn_reject);
        if (rejectBtn != null) {
            rejectBtn.setOnClickListener(v -> {
                Toast.makeText(AdminPlanReviewDetailActivity.this,
                        "审核不通过",
                        Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}