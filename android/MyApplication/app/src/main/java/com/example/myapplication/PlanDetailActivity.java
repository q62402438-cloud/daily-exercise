package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.ExerciseRecord;
import com.example.myapplication.model.FavoriteEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanDetailActivity extends AppCompatActivity {
    private static final String TAG = "PlanDetailActivity";
    private Integer planId;
    private Integer userId;
    private boolean isCollected;
    private Integer favoriteId;
    private ApiService apiService;
    private TrainingPlan currentPlan;

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

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        userId = new SessionManager(this).getUserId();

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            planId = parsePlanId(bundle.getString("plan_id", ""));
        }

        initViews();
        loadPlanDetails();
    }

    private void initViews() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button collectBtn = findViewById(R.id.btn_collect_plan);
        if (collectBtn != null) {
            collectBtn.setOnClickListener(v -> toggleCollect());
        }

        Button startBtn = findViewById(R.id.btn_start_plan);
        if (startBtn != null) {
            startBtn.setOnClickListener(v -> startPlan());
        }

        Button editBtn = findViewById(R.id.btn_edit_plan);
        if (editBtn != null) {
            editBtn.setOnClickListener(v -> editPlan());
        }

        Button auditBtn = findViewById(R.id.btn_submit_audit);
        if (auditBtn != null) {
            auditBtn.setOnClickListener(v -> submitAudit());
        }

        Button deleteBtn = findViewById(R.id.btn_delete_plan);
        if (deleteBtn != null) {
            deleteBtn.setOnClickListener(v -> confirmDelete());
        }

        updateCollectButton();
    }

    private void loadPlanDetails() {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvSportType = findViewById(R.id.tv_sport_type);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        TextView tvDailyExercise = findViewById(R.id.tv_daily_exercise);
        TextView tvExecutionStatus = findViewById(R.id.tv_execution_status);
        TextView tvShelfStatus = findViewById(R.id.tv_shelf_status);
        TextView tvPercentage = findViewById(R.id.tv_percentage);
        TextView tvDetail = findViewById(R.id.tv_detail);

        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            planId = parsePlanId(bundle.getString("plan_id", ""));

            String planName = bundle.getString("plan_name", "初级跑步计划");
            String sportType = bundle.getString("sport_type", "跑步");
            String startDate = bundle.getString("start_date", "2025-01-01");
            String endDate = bundle.getString("end_date", "2025-01-30");
            String dailyExercise = bundle.getString("daily_exercise", "30");

            if (tvPlanName != null) tvPlanName.setText(planName);
            if (tvSportType != null) tvSportType.setText(sportType);
            if (tvStartDate != null) tvStartDate.setText(startDate);
            if (tvEndDate != null) tvEndDate.setText(endDate);
            if (tvDailyExercise != null) tvDailyExercise.setText(dailyExercise + " 分钟");
            if (tvDetail != null) tvDetail.setText("暂无详情");
        }

        if (planId != null) {
            loadPlanById(planId);
            refreshFavoriteState();
        }
    }

    private void loadPlanById(Integer id) {
        TrainingPlan request = new TrainingPlan();
        request.setPlanID(id);
        apiService.getTrainingPlanById(request).enqueue(new Callback<Result<TrainingPlan>>() {
            @Override
            public void onResponse(Call<Result<TrainingPlan>> call, Response<Result<TrainingPlan>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    currentPlan = response.body().getData();
                    renderPlanDetail(currentPlan);
                }
            }

            @Override
            public void onFailure(Call<Result<TrainingPlan>> call, Throwable t) {
                Log.e(TAG, "load plan detail failed", t);
            }
        });
    }

    private void renderPlanDetail(TrainingPlan plan) {
        TextView tvPlanName = findViewById(R.id.tv_plan_name);
        TextView tvSportType = findViewById(R.id.tv_sport_type);
        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);
        TextView tvDailyExercise = findViewById(R.id.tv_daily_exercise);
        TextView tvExecutionStatus = findViewById(R.id.tv_execution_status);
        TextView tvShelfStatus = findViewById(R.id.tv_shelf_status);
        TextView tvPercentage = findViewById(R.id.tv_percentage);
        TextView tvDetail = findViewById(R.id.tv_detail);
        Button submitAuditBtn = findViewById(R.id.btn_submit_audit);
        Button startPlanBtn = findViewById(R.id.btn_start_plan);

        if (tvPlanName != null) tvPlanName.setText(plan.getPlanName() != null ? plan.getPlanName() : "");
        if (tvSportType != null) tvSportType.setText(plan.getSportName() != null ? plan.getSportName() : "");

        String startTime = plan.getStartTime();
        String endTime = plan.getEndTime();
        if (tvStartDate != null) tvStartDate.setText(startTime != null ? startTime.split("T")[0] : "-");
        if (tvEndDate != null) tvEndDate.setText(endTime != null ? endTime.split("T")[0] : "-");

        String exerciseAmount = plan.getExerciseAmount() != null ? plan.getExerciseAmount() : "30";
        if (tvDailyExercise != null) tvDailyExercise.setText(exerciseAmount + " 分钟");

        Integer planType = plan.getPlanType();
        int executionDigit = planType != null ? (planType / 10) % 10 : 0;
        int statusDigit = planType != null ? planType % 10 : 0;

        String executionStatus;
        if (executionDigit == 0) {
            executionStatus = "未开始";
        } else if (executionDigit == 1) {
            executionStatus = "执行中";
        } else if (executionDigit == 2) {
            executionStatus = "已完毕";
        } else {
            executionStatus = "未知";
        }
        if (tvExecutionStatus != null) tvExecutionStatus.setText(executionStatus);

        String shelfStatus;
        if (statusDigit == 0) {
            shelfStatus = "未提交审核";
        } else if (statusDigit == 1) {
            shelfStatus = "审核中";
        } else if (statusDigit == 2) {
            shelfStatus = "已通过";
        } else {
            shelfStatus = "未知";
        }
        if (tvShelfStatus != null) tvShelfStatus.setText(shelfStatus);

        int percentage = 0;
        if (tvPercentage != null) tvPercentage.setText(percentage + "%");

        String startDate = startTime != null ? startTime.split("T")[0] : null;
        String endDate = endTime != null ? endTime.split("T")[0] : null;
        Log.d(TAG, "renderPlanDetail: startDate=" + startDate + ", endDate=" + endDate + ", userId=" + userId);
        if (startDate != null && endDate != null && userId != null && currentPlan != null) {
            Log.d(TAG, "Calling loadPlanCompletionRate");
            loadPlanCompletionRate(startDate, endDate);
        }

        if (tvDetail != null) tvDetail.setText(plan.getDetail() != null ? plan.getDetail() : "暂无详情");

        if (submitAuditBtn != null) {
            if (statusDigit == 0) {
                submitAuditBtn.setVisibility(View.VISIBLE);
            } else {
                submitAuditBtn.setVisibility(View.GONE);
            }
        }

        if (startPlanBtn != null) {
            if (executionDigit == 1) {
                startPlanBtn.setVisibility(View.GONE);
            } else {
                startPlanBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    private void startPlan() {
        if (planId == null) {
            Toast.makeText(this, "计划加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("开始训练")
                .setMessage("确定要开始执行此计划吗？")
                .setPositiveButton("确定", (dialog, which) -> executeStartPlan())
                .setNegativeButton("取消", null)
                .show();
    }

    private void executeStartPlan() {
        if (planId == null) return;

        Integer currentPlanType = currentPlan != null ? currentPlan.getPlanType() : 0;
        int statusDigit = currentPlanType % 10;
        int newPlanType = 10 + statusDigit;

        TrainingPlan request = new TrainingPlan();
        request.setPlanID(planId);
        request.setPlanType(newPlanType);
        request.setPlanName(currentPlan != null ? currentPlan.getPlanName() : "");
        request.setStartTime(currentPlan != null ? currentPlan.getStartTime() : "");
        request.setEndTime(currentPlan != null ? currentPlan.getEndTime() : "");
        request.setSportName(currentPlan != null ? currentPlan.getSportName() : "");
        request.setExerciseAmount(currentPlan != null ? currentPlan.getExerciseAmount() : "");
        request.setDetail(currentPlan != null ? currentPlan.getDetail() : "");

        apiService.updateTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(PlanDetailActivity.this, "计划已开始执行", Toast.LENGTH_SHORT).show();
                    loadPlanById(planId);
                } else {
                    Toast.makeText(PlanDetailActivity.this, "开始计划失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e(TAG, "start plan failed", t);
                Toast.makeText(PlanDetailActivity.this, "开始计划失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editPlan() {
        if (planId == null) {
            Toast.makeText(this, "计划加载失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(PlanDetailActivity.this, EditPlanActivity.class);
        intent.putExtra("plan_id", String.valueOf(planId));
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void submitAudit() {
        if (planId == null) {
            Toast.makeText(this, "计划加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("提交审核")
                .setMessage("确定要提交此计划审核吗？")
                .setPositiveButton("确定", (dialog, which) -> executeSubmitAudit())
                .setNegativeButton("取消", null)
                .show();
    }

    private void executeSubmitAudit() {
        if (planId == null || currentPlan == null) return;

        Integer currentPlanType = currentPlan.getPlanType();
        int executionDigit = currentPlanType != null ? (currentPlanType / 10) % 10 : 0;
        int newPlanType = executionDigit * 10 + 1;

        TrainingPlan request = new TrainingPlan();
        request.setPlanID(planId);
        request.setPlanName(currentPlan.getPlanName());
        request.setPlanType(newPlanType);
        request.setStartTime(currentPlan.getStartTime());
        request.setEndTime(currentPlan.getEndTime());
        request.setSportName(currentPlan.getSportName());
        request.setExerciseAmount(currentPlan.getExerciseAmount());
        request.setDetail(currentPlan.getDetail());

        apiService.updateTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(PlanDetailActivity.this, "已提交审核", Toast.LENGTH_SHORT).show();
                    loadPlanById(planId);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "提交失败";
                    Toast.makeText(PlanDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(PlanDetailActivity.this, "提交失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDelete() {
        if (planId == null) {
            Toast.makeText(this, "计划加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("删除计划")
                .setMessage("确定要删除这个计划吗？删除后将无法恢复。")
                .setPositiveButton("取消", null)
                .setNegativeButton("删除", (dialog, which) -> deletePlan())
                .show();
    }

    private void deletePlan() {
        if (planId == null) return;

        TrainingPlan request = new TrainingPlan();
        request.setPlanID(planId);

        apiService.deleteTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(PlanDetailActivity.this, "计划已删除", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "删除失败";
                    Toast.makeText(PlanDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(PlanDetailActivity.this, "删除失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleCollect() {
        if (userId == null || planId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCollected) {
            if (favoriteId == null) return;
            FavoriteEntity request = new FavoriteEntity();
            request.setFavoriteID(favoriteId);
            apiService.deleteFavorite(request).enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                        isCollected = false;
                        favoriteId = null;
                        updateCollectButton();
                        Toast.makeText(PlanDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlanDetailActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Toast.makeText(PlanDetailActivity.this, "取消收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            FavoriteEntity request = new FavoriteEntity();
            request.setUserID(userId);
            request.setTargetID(planId);
            request.setTargetType(1);
            apiService.addFavorite(request).enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                        Toast.makeText(PlanDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        refreshFavoriteState();
                    } else {
                        Toast.makeText(PlanDetailActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Toast.makeText(PlanDetailActivity.this, "收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void refreshFavoriteState() {
        if (userId == null || planId == null) {
            updateCollectButton();
            return;
        }
        FavoriteEntity request = new FavoriteEntity();
        request.setUserID(userId);
        request.setTargetID(planId);
        request.setTargetType(1);
        apiService.checkFavorite(request).enqueue(new Callback<Result<FavoriteEntity>>() {
            @Override
            public void onResponse(Call<Result<FavoriteEntity>> call, Response<Result<FavoriteEntity>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    Integer returnedFavoriteId = response.body().getData().getFavoriteID();
                    if (returnedFavoriteId != null && returnedFavoriteId > 0) {
                        isCollected = true;
                        favoriteId = returnedFavoriteId;
                    } else {
                        isCollected = false;
                        favoriteId = null;
                    }
                } else {
                    isCollected = false;
                    favoriteId = null;
                }
                updateCollectButton();
            }

            @Override
            public void onFailure(Call<Result<FavoriteEntity>> call, Throwable t) {
                isCollected = false;
                favoriteId = null;
                updateCollectButton();
            }
        });
    }

    private void updateCollectButton() {
        Button collectBtn = findViewById(R.id.btn_collect_plan);
        if (collectBtn != null) {
            collectBtn.setText(isCollected ? "取消收藏" : "收藏计划");
        }
    }

    private Integer parsePlanId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (planId != null) {
            loadPlanById(planId);
            refreshFavoriteState();
        }
    }

    private void loadPlanCompletionRate(String startDate, String endDate) {
        TextView tvPercentage = findViewById(R.id.tv_percentage);
        if (tvPercentage != null) {
            tvPercentage.setText("0%");
        }
        
        ExerciseRecord request = new ExerciseRecord();
        request.setUserID(userId);
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        apiService.getExerciseRecordsByDateRange(request).enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                Log.d(TAG, "getExerciseRecordsByDateRange response: " + response.isSuccessful() + ", code=" + (response.body() != null ? response.body().getCode() : "null"));
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    List<ExerciseRecord> records = response.body().getData();
                    
                    java.util.Set<String> checkInDays = new java.util.HashSet<>();
                    
                    Log.d(TAG, "=== 开始筛选打卡记录 ===");
                    Log.d(TAG, "目标计划ID (planId): " + planId);
                    Log.d(TAG, "总记录数: " + records.size());
                    
                    int matchingRecords = 0;
                    int skippedRecords = 0;
                    
                    for (ExerciseRecord record : records) {
                        Integer recordType = record.getRecordType();
                        Integer eventId = record.getEventID();
                        String sportsDate = record.getSportsDate();
                        
                        if (recordType == null) {
                            Log.v(TAG, "✗ 跳过记录: recordType=null, eventId=" + eventId + ", 日期=" + sportsDate);
                            skippedRecords++;
                            continue;
                        }
                        
                        if (recordType == 0) {
                            Log.v(TAG, "✗ 赛事记录 (recordType=0): eventId=" + eventId + ", 日期=" + sportsDate);
                            skippedRecords++;
                            continue;
                        }
                        
                        if (recordType == 1 && eventId != null && eventId.equals(planId)) {
                            if (sportsDate != null && !sportsDate.isEmpty()) {
                                String datePart = sportsDate.contains("T") ? sportsDate.split("T")[0] : sportsDate.split(" ")[0];
                                if (isDateInRange(datePart, startDate, endDate)) {
                                    checkInDays.add(datePart);
                                    Log.d(TAG, "✓ 符合当前计划的打卡记录: 日期=" + datePart + ", eventId(planId)=" + eventId);
                                    matchingRecords++;
                                } else {
                                    Log.v(TAG, "✗ 日期不在范围内: 日期=" + datePart + ", 范围=" + startDate + " 至 " + endDate);
                                    skippedRecords++;
                                }
                            }
                        } else {
                            Log.v(TAG, "✗ 其他计划记录: eventId=" + eventId + ", 日期=" + sportsDate + " (不匹配目标planId=" + planId + ")");
                            skippedRecords++;
                        }
                    }
                    
                    Log.d(TAG, "=== 筛选统计结果 ===");
                    Log.d(TAG, "符合当前计划的记录数: " + matchingRecords);
                    Log.d(TAG, "赛事记录数 (跳过): " + (skippedRecords - (records.size() - matchingRecords - skippedRecords)));
                    Log.d(TAG, "其他计划记录数 (跳过): " + (records.size() - matchingRecords - skippedRecords));
                    Log.d(TAG, "去重后的打卡天数: " + checkInDays.size());
                    
                    int checkInDaysCount = checkInDays.size();
                    int totalDays = calculateDaysBetween(startDate, endDate);
                    int calculatedPercentage = 0;
                    if (totalDays > 0) {
                        calculatedPercentage = Math.min(100, Math.round((float) checkInDaysCount / totalDays * 100));
                    }
                    
                    Log.d(TAG, "=== 计划完成率计算详情 ===");
                    Log.d(TAG, "计划ID: " + planId);
                    Log.d(TAG, "日期范围: " + startDate + " 至 " + endDate);
                    Log.d(TAG, "该时间段内所有计划记录数: " + records.size());
                    Log.d(TAG, "符合当前计划的打卡天数: " + checkInDaysCount);
                    Log.d(TAG, "计划总天数: " + totalDays);
                    Log.d(TAG, "完成率: " + calculatedPercentage + "%");
                    Log.d(TAG, "打卡日期列表: " + checkInDays.toString());
                    Log.d(TAG, "================================");
                    
                    final int percentage = calculatedPercentage;
                    if (tvPercentage != null) {
                        tvPercentage.setText(percentage + "%");
                    }
                    
                    updatePlanPercentage(percentage);
                } else {
                    Log.d(TAG, "getExerciseRecordsByDateRange failed or no data");
                }
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                Log.e(TAG, "load plan completion rate failed", t);
            }
        });
    }
    
    private boolean isDateInRange(String dateStr, String startDate, String endDate) {
        try {
            java.time.LocalDate date = java.time.LocalDate.parse(dateStr);
            java.time.LocalDate start = java.time.LocalDate.parse(startDate);
            java.time.LocalDate end = java.time.LocalDate.parse(endDate);
            return !date.isBefore(start) && !date.isAfter(end);
        } catch (Exception e) {
            Log.e(TAG, "isDateInRange error: " + e.getMessage());
            return false;
        }
    }

    private void updatePlanPercentage(int percentage) {
        Log.d(TAG, "updatePlanPercentage called: planId=" + planId + ", percentage=" + percentage);
        if (planId == null) {
            Log.d(TAG, "updatePlanPercentage skipped: planId is null");
            return;
        }

        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("planID", planId);
        requestBody.put("percentage", percentage);

        apiService.updatePlanProgress(requestBody).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Log.d(TAG, "计划完成率已更新到数据库: " + percentage + "%");
                } else {
                    Log.d(TAG, "更新计划完成率失败: code=" + (response.body() != null ? response.body().getCode() : "null"));
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e(TAG, "更新计划完成率失败", t);
            }
        });
    }

    private int calculateDaysBetween(String startDate, String endDate) {
        try {
            String[] startParts = startDate.split("-");
            String[] endParts = endDate.split("-");

            Calendar startCal = Calendar.getInstance();
            startCal.set(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]) - 1, Integer.parseInt(startParts[2]));

            Calendar endCal = Calendar.getInstance();
            endCal.set(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]) - 1, Integer.parseInt(endParts[2]));

            long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
            return (int) (diffMillis / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}