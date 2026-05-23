package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.cardview.widget.CardView;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartPlanActivity extends AppCompatActivity {
    private Spinner spPlan;
    private CardView cardPlanDetail;
    private LinearLayout layoutEmpty;
    private TextView tvPlanName;
    private TextView tvStatus;
    private TextView tvSportType;
    private TextView tvDuration;
    private TextView tvDays;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private ProgressBar progressBar;
    private TextView tvProgress;
    private TextView tvDetail;
    private Button btnStart;

    private List<TrainingPlan> planList = new ArrayList<>();
    private TrainingPlan selectedPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadPlans();
    }

    private void initViews() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        spPlan = findViewById(R.id.sp_plan);
        cardPlanDetail = findViewById(R.id.card_plan_detail);
        layoutEmpty = findViewById(R.id.layout_empty);
        tvPlanName = findViewById(R.id.tv_plan_name);
        tvStatus = findViewById(R.id.tv_status);
        tvSportType = findViewById(R.id.tv_sport_type);
        tvDuration = findViewById(R.id.tv_duration);
        tvDays = findViewById(R.id.tv_days);
        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        progressBar = findViewById(R.id.progress_bar);
        tvProgress = findViewById(R.id.tv_progress);
        tvDetail = findViewById(R.id.tv_detail);
        btnStart = findViewById(R.id.btn_start);

        Button cancelBtn = findViewById(R.id.btn_cancel);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        if (btnStart != null) {
            btnStart.setOnClickListener(v -> startTraining());
        }

        if (spPlan != null) {
            spPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0 && position <= planList.size()) {
                        selectedPlan = planList.get(position - 1);
                        showPlanDetail(selectedPlan);
                    } else {
                        selectedPlan = null;
                        hidePlanDetail();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    selectedPlan = null;
                    hidePlanDetail();
                }
            });
        }
    }

    private void loadPlans() {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUserID(userId);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getTrainingPlansByUser(user).enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    planList = response.body().getData();
                    setupSpinner();

                    String preSelectedPlanId = getIntent().getStringExtra("plan_id");
                    if (preSelectedPlanId != null) {
                        for (int i = 0; i < planList.size(); i++) {
                            if (String.valueOf(planList.get(i).getPlanID()).equals(preSelectedPlanId)) {
                                spPlan.setSelection(i + 1);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                Toast.makeText(StartPlanActivity.this, "加载计划失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinner() {
        List<String> planNames = new ArrayList<>();
        planNames.add("-- 请选择训练计划 --");
        for (TrainingPlan plan : planList) {
            planNames.add(plan.getPlanName() != null ? plan.getPlanName() : "未命名计划");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, planNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spPlan != null) {
            spPlan.setAdapter(adapter);
        }
    }

    private void showPlanDetail(TrainingPlan plan) {
        if (cardPlanDetail != null) cardPlanDetail.setVisibility(View.VISIBLE);
        if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);

        if (tvPlanName != null) tvPlanName.setText(plan.getPlanName() != null ? plan.getPlanName() : "");
        if (tvSportType != null) tvSportType.setText(plan.getSportName() != null ? plan.getSportName() : "-");

        String exerciseAmount = plan.getExerciseAmount() != null ? plan.getExerciseAmount() : "30";
        if (tvDuration != null) tvDuration.setText(exerciseAmount + " 分钟");

        String startTime = plan.getStartTime();
        String endTime = plan.getEndTime();
        String startDate = startTime != null ? startTime.split("T")[0] : "-";
        String endDate = endTime != null ? endTime.split("T")[0] : "-";

        if (tvStartDate != null) tvStartDate.setText(startDate);
        if (tvEndDate != null) tvEndDate.setText(endDate);

        int days = 0;
        if (startTime != null && endTime != null) {
            days = getDaysBetween(startDate, endDate);
        }
        if (tvDays != null) tvDays.setText(days + " 天");

        int percentage = 0;
        if (plan.getDailyCalorie() != null) {
            try {
                percentage = Integer.parseInt(plan.getDailyCalorie());
            } catch (NumberFormatException ignored) {}
        }
        if (progressBar != null) progressBar.setProgress(percentage);
        if (tvProgress != null) tvProgress.setText("已完成 " + percentage + "%");

        if (tvDetail != null) tvDetail.setText(plan.getDetail() != null ? plan.getDetail() : "暂无详情");

        Integer planType = plan.getPlanType();
        int executionDigit = planType != null ? (planType / 10) % 10 : 0;

        if (tvStatus != null) {
            if (executionDigit == 0) {
                tvStatus.setText("未开始");
                tvStatus.getBackground().setTint(android.graphics.Color.parseColor("#F1C40F"));
            } else if (executionDigit == 1) {
                tvStatus.setText("进行中");
                tvStatus.getBackground().setTint(android.graphics.Color.parseColor("#27AE60"));
            } else {
                tvStatus.setText("已完成");
                tvStatus.getBackground().setTint(android.graphics.Color.parseColor("#3498DB"));
            }
        }

        if (btnStart != null) {
            if (executionDigit == 0) {
                btnStart.setText("开始训练");
                btnStart.setEnabled(true);
            } else if (executionDigit == 1) {
                btnStart.setText("继续训练");
                btnStart.setEnabled(true);
            } else {
                btnStart.setText("计划已完成");
                btnStart.setEnabled(false);
            }
        }
    }

    private void hidePlanDetail() {
        if (cardPlanDetail != null) cardPlanDetail.setVisibility(View.GONE);
        if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
    }

    private int getDaysBetween(String startDateStr, String endDateStr) {
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date startDate = sdf.parse(startDateStr);
            java.util.Date endDate = sdf.parse(endDateStr);
            if (startDate == null || endDate == null) return 0;
            long diff = endDate.getTime() - startDate.getTime();
            return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    private void startTraining() {
        if (selectedPlan == null) {
            Toast.makeText(this, "请先选择一个计划", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer planType = selectedPlan.getPlanType();
        int statusDigit = planType != null ? planType % 10 : 0;
        int executionDigit = planType != null ? (planType / 10) % 10 : 0;

        String confirmMessage;
        if (executionDigit == 0) {
            confirmMessage = "确定要开始 \"" + selectedPlan.getPlanName() + "\" 训练计划吗？\n\n开始后您可以查看训练进度并记录每日训练。";
        } else {
            confirmMessage = "确定要继续 \"" + selectedPlan.getPlanName() + "\" 训练计划吗？";
        }

        new AlertDialog.Builder(this)
                .setTitle("开始训练")
                .setMessage(confirmMessage)
                .setPositiveButton("确定", (dialog, which) -> executeStartPlan())
                .setNegativeButton("取消", null)
                .show();
    }

    private void executeStartPlan() {
        if (selectedPlan == null || selectedPlan.getPlanID() == null) return;

        Integer currentPlanType = selectedPlan.getPlanType();
        int statusDigit = currentPlanType != null ? currentPlanType % 10 : 0;
        int newPlanType = 10 + statusDigit;

        TrainingPlan request = new TrainingPlan();
        request.setPlanID(selectedPlan.getPlanID());
        request.setPlanName(selectedPlan.getPlanName());
        request.setPlanType(newPlanType);
        request.setStartTime(selectedPlan.getStartTime());
        request.setEndTime(selectedPlan.getEndTime());
        request.setSportName(selectedPlan.getSportName());
        request.setExerciseAmount(selectedPlan.getExerciseAmount());
        request.setDetail(selectedPlan.getDetail());

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.updateTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(StartPlanActivity.this, "训练计划已开始！", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "开始失败";
                    Toast.makeText(StartPlanActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(StartPlanActivity.this, "开始失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}