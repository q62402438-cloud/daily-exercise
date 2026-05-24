package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import com.example.myapplication.model.ExerciseRecord;
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

public class MyPlansActivity extends AppCompatActivity {
    private RecyclerView rvPlans;
    private LinearLayout layoutEmpty;
    private LinearLayout layoutLoading;
    private MyPlanAdapter planAdapter;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_plans);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        initViews();
        loadPlans();
    }

    private void initViews() {
        rvPlans = findViewById(R.id.rv_plans);
        layoutEmpty = findViewById(R.id.layout_empty);
        layoutLoading = findViewById(R.id.layout_loading);

        if (rvPlans != null) {
            rvPlans.setLayoutManager(new LinearLayoutManager(this));
            planAdapter = new MyPlanAdapter(new ArrayList<>());
            rvPlans.setAdapter(planAdapter);
        }

        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button createBtn = findViewById(R.id.btn_create);
        if (createBtn != null) {
            createBtn.setOnClickListener(v -> {
                Intent intent = new Intent(MyPlansActivity.this, CreateExercisePlanActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlans();
    }

    private void loadPlans() {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading();

        User user = new User();
        user.setUserID(userId);

        apiService.getTrainingPlansByUser(user).enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                hideLoading();
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    List<TrainingPlan> plans = response.body().getData();
                    loadExerciseRecordsAndUpdatePlans(plans);
                } else {
                    showEmpty();
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                hideLoading();
                Toast.makeText(MyPlansActivity.this, "加载失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                showEmpty();
            }
        });
    }

    private void loadExerciseRecordsAndUpdatePlans(List<TrainingPlan> plans) {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) return;

        User user = new User();
        user.setUserID(userId);

        apiService.getExerciseRecordsByUser(user).enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                List<ExerciseRecord> allRecords = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    allRecords = response.body().getData();
                }
                calculateProgressAndRender(plans, allRecords);
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                calculateProgressAndRender(plans, new ArrayList<>());
            }
        });
    }

    private void calculateProgressAndRender(List<TrainingPlan> plans, List<ExerciseRecord> allRecords) {
        for (TrainingPlan plan : plans) {
            String startTime = plan.getStartTime();
            String endTime = plan.getEndTime();
            Integer planId = plan.getPlanID();

            if (startTime == null || endTime == null) {
                plan.setDailyCalorie("0");
                continue;
            }

            String startDate = startTime.split("T")[0];
            String endDate = endTime.split("T")[0];

            int totalDays = getDaysBetween(startDate, endDate);
            java.util.Set<String> checkedInDates = new java.util.HashSet<>();

            for (ExerciseRecord record : allRecords) {
                if (record.getRecordType() != null && record.getRecordType() == 1
                        && record.getSportsDate() != null
                        && record.getEventID() != null && record.getEventID().equals(planId)) {
                    String recordDate = record.getSportsDate().split(" ")[0].split("T")[0];
                    if (isDateInRange(recordDate, startDate, endDate)) {
                        checkedInDates.add(recordDate);
                    }
                }
            }

            int checkInDays = checkedInDates.size();
            int percentage = totalDays > 0 ? Math.round((float) checkInDays / totalDays * 100) : 0;
            plan.setDailyCalorie(String.valueOf(percentage));
            plan.setDetail(checkInDays + "/" + totalDays);
        }

        if (plans.isEmpty()) {
            showEmpty();
        } else {
            showPlans(plans);
        }
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

    private boolean isDateInRange(String dateStr, String startDateStr, String endDateStr) {
        try {
            dateStr = extractDatePart(dateStr);
            startDateStr = extractDatePart(startDateStr);
            endDateStr = extractDatePart(endDateStr);

            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.LocalDate startDate = java.time.LocalDate.parse(startDateStr);
            java.time.LocalDate endDate = java.time.LocalDate.parse(endDateStr);
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        } catch (Exception e) {
            return false;
        }
    }

    private String extractDatePart(String dateTimeStr) {
        if (dateTimeStr == null) {
            return null;
        }
        String datePart = dateTimeStr.split("T")[0];
        datePart = datePart.split(" ")[0];
        return datePart;
    }

    private void showLoading() {
        if (layoutLoading != null) layoutLoading.setVisibility(View.VISIBLE);
        if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
        if (rvPlans != null) rvPlans.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (layoutLoading != null) layoutLoading.setVisibility(View.GONE);
    }

    private void showEmpty() {
        if (layoutEmpty != null) layoutEmpty.setVisibility(View.VISIBLE);
        if (layoutLoading != null) layoutLoading.setVisibility(View.GONE);
        if (rvPlans != null) rvPlans.setVisibility(View.GONE);
    }

    private void showPlans(List<TrainingPlan> plans) {
        if (layoutEmpty != null) layoutEmpty.setVisibility(View.GONE);
        if (layoutLoading != null) layoutLoading.setVisibility(View.GONE);
        if (rvPlans != null) rvPlans.setVisibility(View.VISIBLE);
        if (planAdapter != null) {
            planAdapter.setPlanList(plans);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    class MyPlanAdapter extends RecyclerView.Adapter<MyPlanAdapter.PlanViewHolder> {
        private List<TrainingPlan> planList;

        public MyPlanAdapter(List<TrainingPlan> planList) {
            this.planList = planList;
        }

        public void setPlanList(List<TrainingPlan> planList) {
            this.planList = planList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_plan, parent, false);
            return new PlanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
            TrainingPlan plan = planList.get(position);
            holder.bind(plan);
        }

        @Override
        public int getItemCount() {
            return planList.size();
        }

        class PlanViewHolder extends RecyclerView.ViewHolder {
            private TextView tvPlanName;
            private TextView tvSportType;
            private TextView tvDuration;
            private TextView tvStatus;
            private TextView tvDescription;
            private ProgressBar progressBar;
            private TextView tvProgress;
            private Button btnDetail;
            private Button btnStart;
            private Button btnEdit;
            private Button btnDelete;

            public PlanViewHolder(@NonNull View itemView) {
                super(itemView);
                tvPlanName = itemView.findViewById(R.id.tv_plan_name);
                tvSportType = itemView.findViewById(R.id.tv_sport_type);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvDescription = itemView.findViewById(R.id.tv_description);
                progressBar = itemView.findViewById(R.id.progress_bar);
                tvProgress = itemView.findViewById(R.id.tv_progress);
                btnDetail = itemView.findViewById(R.id.btn_detail);
                btnStart = itemView.findViewById(R.id.btn_start);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }

            public void bind(TrainingPlan plan) {
                if (tvPlanName != null) tvPlanName.setText(plan.getPlanName() != null ? plan.getPlanName() : "");
                if (tvSportType != null) tvSportType.setText(plan.getSportName() != null ? plan.getSportName() : "");

                String startTime = plan.getStartTime();
                String endTime = plan.getEndTime();
                if (startTime != null && endTime != null) {
                    String startDate = startTime.split("T")[0];
                    String endDate = endTime.split("T")[0];
                    int days = getDaysBetween(startDate, endDate);
                    if (tvDuration != null) tvDuration.setText(days + "天");
                }

                Integer planType = plan.getPlanType();
                int executionDigit = planType != null ? (planType / 10) % 10 : 0;

                String statusText;
                int statusColor;
                if (executionDigit == 0) {
                    statusText = "未开始";
                    statusColor = android.graphics.Color.parseColor("#95A5A6");
                } else if (executionDigit == 1) {
                    statusText = "进行中";
                    statusColor = android.graphics.Color.parseColor("#27AE60");
                } else {
                    statusText = "已完成";
                    statusColor = android.graphics.Color.parseColor("#3498DB");
                }
                if (tvStatus != null) {
                    tvStatus.setText(statusText);
                    tvStatus.getBackground().setTint(statusColor);
                }

                if (tvDescription != null) {
                    tvDescription.setText(plan.getDetail() != null ? plan.getDetail() : "");
                }

                int percentage = 0;
                if (plan.getDailyCalorie() != null) {
                    try {
                        percentage = Integer.parseInt(plan.getDailyCalorie());
                    } catch (NumberFormatException ignored) {}
                }
                if (progressBar != null) {
                    progressBar.setProgress(percentage);
                }

                String progressInfo = plan.getDetail() != null ? plan.getDetail() : "0/0";
                if (tvProgress != null) {
                    tvProgress.setText("已打卡 " + progressInfo + " 天 (" + percentage + "%)");
                }

                Integer planId = plan.getPlanID();

                if (btnDetail != null) {
                    btnDetail.setOnClickListener(v -> {
                        Intent intent = new Intent(MyPlansActivity.this, PlanDetailActivity.class);
                        intent.putExtra("plan_id", String.valueOf(planId));
                        intent.putExtra("plan_name", plan.getPlanName());
                        intent.putExtra("sport_type", plan.getSportName());
                        intent.putExtra("start_date", startTime != null ? startTime.split("T")[0] : "");
                        intent.putExtra("end_date", endTime != null ? endTime.split("T")[0] : "");
                        intent.putExtra("daily_exercise", plan.getExerciseAmount());
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    });
                }

                if (btnStart != null) {
                    if (executionDigit == 0) {
                        btnStart.setText("开始");
                        btnStart.setVisibility(View.VISIBLE);
                        btnStart.setOnClickListener(v -> {
                            Intent intent = new Intent(MyPlansActivity.this, StartPlanActivity.class);
                            intent.putExtra("plan_id", String.valueOf(planId));
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                        });
                    } else {
                        btnStart.setVisibility(View.GONE);
                    }
                }

                if (btnEdit != null) {
                    btnEdit.setOnClickListener(v -> {
                        Intent intent = new Intent(MyPlansActivity.this, EditPlanActivity.class);
                        intent.putExtra("plan_id", String.valueOf(planId));
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    });
                }

                if (btnDelete != null) {
                    btnDelete.setOnClickListener(v -> confirmDelete(plan));
                }
            }
        }
    }

    private void confirmDelete(TrainingPlan plan) {
        new AlertDialog.Builder(this)
                .setTitle("删除计划")
                .setMessage("确定要删除这个计划吗？删除后将无法恢复。")
                .setPositiveButton("取消", null)
                .setNegativeButton("删除", (dialog, which) -> deletePlan(plan))
                .show();
    }

    private void deletePlan(TrainingPlan plan) {
        if (plan.getPlanID() == null) return;

        TrainingPlan request = new TrainingPlan();
        request.setPlanID(plan.getPlanID());

        apiService.deleteTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(MyPlansActivity.this, "计划已删除", Toast.LENGTH_SHORT).show();
                    loadPlans();
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "删除失败";
                    Toast.makeText(MyPlansActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(MyPlansActivity.this, "删除失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}