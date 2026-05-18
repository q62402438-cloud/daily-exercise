package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PlanDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plan_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadPlanDetails();

        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button startPlanBtn = findViewById(R.id.btn_start_plan);
        if (startPlanBtn != null) {
            startPlanBtn.setOnClickListener(v -> {
                TextView planName = findViewById(R.id.tv_plan_name);
                String name = planName != null ? planName.getText().toString() : "计划";
                Toast.makeText(PlanDetailActivity.this,
                        "正在开始执行：" + name,
                        Toast.LENGTH_SHORT).show();

                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void loadPlanDetails() {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvSportType = findViewById(R.id.tv_sport_type);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        TextView tvDailyExercise = findViewById(R.id.tv_daily_exercise);
        TextView tvDailyCalorie = findViewById(R.id.tv_daily_calorie);
        TextView tvVisibility = findViewById(R.id.tv_visibility);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            String planName = bundle.getString("plan_name", "初级跑步计划");
            String sportType = bundle.getString("sport_type", "跑步");
            String startDate = bundle.getString("start_date", "2025-01-01");
            String endDate = bundle.getString("end_date", "2025-01-30");
            String dailyExercise = bundle.getString("daily_exercise", "5");
            String dailyCalorie = bundle.getString("daily_calorie", "300");
            boolean isPublic = bundle.getBoolean("is_public", true);

            if (tvPlanName != null) tvPlanName.setText(planName);
            if (tvSportType != null) tvSportType.setText(sportType);
            if (tvStartDate != null) tvStartDate.setText(startDate);
            if (tvEndDate != null) tvEndDate.setText(endDate);
            if (tvDailyExercise != null) tvDailyExercise.setText(dailyExercise + " 公里");
            if (tvDailyCalorie != null) tvDailyCalorie.setText(dailyCalorie + " kcal");
            if (tvVisibility != null) {
                tvVisibility.setText(isPublic ? "公开" : "私密");
                tvVisibility.setTextColor(getResources().getColor(
                        isPublic ? android.R.color.holo_green_dark : android.R.color.darker_gray));
            }
        } else {
            setSampleData();
        }
    }

    private void setSampleData() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            String planId = getIntent().getExtras().getString("plan_id", "");

            switch (planId) {
                case "running_plan":
                    setPlanData("初级跑步计划", "跑步", "2025-01-01", "2025-01-30", "5", "300", true);
                    break;
                case "yoga_plan":
                    setPlanData("清晨瑜伽放松计划", "瑜伽", "2025-02-01", "2025-02-28", "30", "150", true);
                    break;
                default:
                    setPlanData("初级跑步计划", "跑步", "2025-01-01", "2025-01-30", "5", "300", true);
                    break;
            }
        } else {
            setPlanData("初级跑步计划", "跑步", "2025-01-01", "2025-01-30", "5", "300", true);
        }
    }

    private void setPlanData(String name, String sportType, String startDate,
                            String endDate, String exercise, String calorie, boolean isPublic) {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvSportType = findViewById(R.id.tv_sport_type);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        TextView tvDailyExercise = findViewById(R.id.tv_daily_exercise);
        TextView tvDailyCalorie = findViewById(R.id.tv_daily_calorie);
        TextView tvVisibility = findViewById(R.id.tv_visibility);

        if (tvPlanName != null) tvPlanName.setText(name);
        if (tvSportType != null) tvSportType.setText(sportType);
        if (tvStartDate != null) tvStartDate.setText(startDate);
        if (tvEndDate != null) tvEndDate.setText(endDate);
        if (tvDailyExercise != null) tvDailyExercise.setText(exercise + " 公里");
        if (tvDailyCalorie != null) tvDailyCalorie.setText(calorie + " kcal");
        if (tvVisibility != null) {
            tvVisibility.setText(isPublic ? "公开" : "私密");
            tvVisibility.setTextColor(getResources().getColor(
                    isPublic ? android.R.color.holo_green_dark : android.R.color.darker_gray));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
