package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
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

import com.example.myapplication.model.ExerciseRecord;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckInRecordsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ExerciseRecordAdapter adapter;
    private List<ExerciseRecord> records = new ArrayList<>();
    private TextView tvTotalDays;
    private TextView tvTotalDuration;
    private TextView tvTotalCalorie;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_in_records);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadRecords();
    }

    private void initViews() {
        RelativeLayout backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        tvTotalDays = findViewById(R.id.tv_total_days);
        tvTotalDuration = findViewById(R.id.tv_total_duration);
        tvTotalCalorie = findViewById(R.id.tv_total_calorie);
        emptyState = findViewById(R.id.empty_state);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseRecordAdapter(records);
        recyclerView.setAdapter(adapter);

        adapter.setOnDeleteClickListener((record, position) -> showDeleteConfirmDialog(record, position));

        Button btnDateSearch = findViewById(R.id.btn_date_search);
        if (btnDateSearch != null) {
            btnDateSearch.setOnClickListener(v -> {
                Intent intent = new Intent(CheckInRecordsActivity.this, DateRecordActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void showDeleteConfirmDialog(ExerciseRecord record, int position) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除这条打卡记录吗？")
                .setPositiveButton("取消", null)
                .setNegativeButton("确定", (dialog, which) -> deleteRecord(record, position))
                .show();
    }

    private void deleteRecord(ExerciseRecord record, int position) {
        Integer recordId = record.getRecordID();
        if (recordId == null) {
            Toast.makeText(this, "记录ID无效", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer recordType = record.getRecordType();
        Integer planId = record.getPlanID();
        Log.d("CheckInRecords", "deleteRecord: recordId=" + recordId + ", recordType=" + recordType + ", planId=" + planId);

        if (recordType != null && recordType == 1 && planId != null) {
            Log.d("CheckInRecords", "Deleting plan record, fetching plan dates...");
            fetchPlanDatesAndUpdatePercentage(planId);
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<String>> call = apiService.deleteExerciseRecord(recordId);

        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(CheckInRecordsActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        records.remove(position);
                        adapter.notifyItemRemoved(position);
                        updateStatistics();
                        if (records.isEmpty()) {
                            showEmptyState();
                        }
                    } else {
                        Toast.makeText(CheckInRecordsActivity.this, result.getMessage() != null ? result.getMessage() : "删除失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckInRecordsActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e("CheckInRecords", "删除打卡记录失败", t);
                Toast.makeText(CheckInRecordsActivity.this, "删除失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchPlanDatesAndUpdatePercentage(Integer planId) {
        TrainingPlan request = new TrainingPlan();
        request.setPlanID(planId);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<TrainingPlan>> call = apiService.getTrainingPlanById(request);

        call.enqueue(new Callback<Result<TrainingPlan>>() {
            @Override
            public void onResponse(Call<Result<TrainingPlan>> call, Response<Result<TrainingPlan>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    TrainingPlan plan = response.body().getData();
                    String startTime = plan.getStartTime();
                    String endTime = plan.getEndTime();
                    if (startTime != null && endTime != null) {
                        String startDate = extractDatePart(startTime);
                        String endDate = extractDatePart(endTime);
                        recalculateAndUpdatePlanPercentage(planId, startDate, endDate);
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<TrainingPlan>> call, Throwable t) {
                Log.e("CheckInRecords", "获取计划信息失败", t);
            }
        });
    }

    private void recalculateAndUpdatePlanPercentage(Integer planId, String startDate, String endDate) {
        User user = getCurrentUser();
        if (user == null) return;

        ExerciseRecord request = new ExerciseRecord();
        request.setUserID(user.getUserID());
        request.setStartDate(startDate);
        request.setEndDate(endDate);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<ExerciseRecord>>> call = apiService.getExerciseRecordsByDateRange(request);

        call.enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    List<ExerciseRecord> records = response.body().getData();
                    java.util.Set<String> checkInDays = new java.util.HashSet<>();

                    Log.d("CheckInRecords", "=== 开始筛选打卡记录 ===");
                    Log.d("CheckInRecords", "目标计划ID: " + planId);
                    Log.d("CheckInRecords", "总记录数: " + records.size());

                    int matchingRecords = 0;
                    int skippedRecords = 0;

                    for (ExerciseRecord record : records) {
                        Integer recordType = record.getRecordType();
                        Integer eventId = record.getEventID();
                        String sportsDate = record.getSportsDate();

                        if (recordType == null) {
                            skippedRecords++;
                            continue;
                        }

                        if (recordType == 0) {
                            skippedRecords++;
                            continue;
                        }

                        if (recordType == 1 && eventId != null && eventId.equals(planId)) {
                            if (sportsDate != null && !sportsDate.isEmpty()) {
                                String datePart = sportsDate.contains("T") ? sportsDate.split("T")[0] : sportsDate.split(" ")[0];
                                if (isDateInRange(datePart, startDate, endDate)) {
                                    checkInDays.add(datePart);
                                    matchingRecords++;
                                } else {
                                    skippedRecords++;
                                }
                            }
                        } else {
                            skippedRecords++;
                        }
                    }

                    Log.d("CheckInRecords", "符合当前计划的记录数: " + matchingRecords);
                    Log.d("CheckInRecords", "跳过的记录数: " + skippedRecords);
                    Log.d("CheckInRecords", "去重后的打卡天数: " + checkInDays.size());

                    int checkInDaysCount = checkInDays.size();
                    int totalDays = calculateDaysBetween(startDate, endDate);
                    int percentage = 0;
                    if (totalDays > 0) {
                        percentage = Math.min(100, Math.round((float) checkInDaysCount / totalDays * 100));
                    }

                    Log.d("CheckInRecords", "=== 计划完成率计算详情 ===");
                    Log.d("CheckInRecords", "计划ID: " + planId);
                    Log.d("CheckInRecords", "日期范围: " + startDate + " 至 " + endDate);
                    Log.d("CheckInRecords", "符合当前计划的打卡天数: " + checkInDaysCount);
                    Log.d("CheckInRecords", "计划总天数: " + totalDays);
                    Log.d("CheckInRecords", "完成率: " + percentage + "%");
                    Log.d("CheckInRecords", "================================");

                    updatePlanPercentage(planId, percentage);
                }
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                Log.e("CheckInRecords", "重新计算计划完成度失败", t);
            }
        });
    }

    private boolean isDateInRange(String dateStr, String startDate, String endDate) {
        try {
            dateStr = extractDatePart(dateStr);
            startDate = extractDatePart(startDate);
            endDate = extractDatePart(endDate);
            
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse(dateStr);
            java.util.Date start = sdf.parse(startDate);
            java.util.Date end = sdf.parse(endDate);
            return !date.before(start) && !date.after(end);
        } catch (java.text.ParseException e) {
            Log.e("CheckInRecords", "日期解析失败", e);
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

    private void updatePlanPercentage(Integer planId, int percentage) {
        if (planId == null) {
            Log.d("CheckInRecords", "updatePlanPercentage skipped: planId is null");
            return;
        }

        java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("planID", planId);
        requestBody.put("percentage", percentage);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<String>> call = apiService.updatePlanProgress(requestBody);

        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Log.d("CheckInRecords", "计划完成率已更新到数据库: " + percentage + "%");
                } else {
                    Log.d("CheckInRecords", "更新计划完成率失败: code=" + (response.body() != null ? response.body().getCode() : "null"));
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e("CheckInRecords", "更新计划完成率失败", t);
            }
        });
    }

    private int calculateDaysBetween(String startDate, String endDate) {
        try {
            startDate = extractDatePart(startDate);
            endDate = extractDatePart(endDate);
            
            String[] startParts = startDate.split("-");
            String[] endParts = endDate.split("-");

            java.util.Calendar startCal = java.util.Calendar.getInstance();
            startCal.set(Integer.parseInt(startParts[0]), Integer.parseInt(startParts[1]) - 1, Integer.parseInt(startParts[2]));

            java.util.Calendar endCal = java.util.Calendar.getInstance();
            endCal.set(Integer.parseInt(endParts[0]), Integer.parseInt(endParts[1]) - 1, Integer.parseInt(endParts[2]));

            long diffMillis = endCal.getTimeInMillis() - startCal.getTimeInMillis();
            return (int) (diffMillis / (1000 * 60 * 60 * 24)) + 1;
        } catch (Exception e) {
            Log.e("CheckInRecords", "calculateDaysBetween error: " + e.getMessage());
            return 0;
        }
    }

    private void loadRecords() {
        User user = getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<ExerciseRecord>>> call = apiService.getExerciseRecordsByUser(user);

        call.enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<ExerciseRecord>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        records = result.getData();
                        fetchMissingPlanNames(records);
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                Log.e("CheckInRecords", "加载打卡记录失败", t);
                showEmptyState();
            }
        });
    }

    private void fetchMissingPlanNames(List<ExerciseRecord> records) {
        List<ExerciseRecord> plansToFetch = new ArrayList<>();

        for (ExerciseRecord record : records) {
            if (record.getRecordType() != null && record.getRecordType() == 1
                    && record.getEventID() != null
                    && (record.getSportName() == null || record.getSportName().isEmpty())) {
                plansToFetch.add(record);
            }
        }

        if (plansToFetch.isEmpty()) {
            adapter.updateData(records);
            updateStatistics();
            showRecords();
            return;
        }

        final int[] pendingCount = {plansToFetch.size()};

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        for (ExerciseRecord record : plansToFetch) {
            TrainingPlan request = new TrainingPlan();
            request.setPlanId(record.getEventID());

            Call<Result<TrainingPlan>> call = apiService.getTrainingPlanById(request);
            call.enqueue(new Callback<Result<TrainingPlan>>() {
                @Override
                public void onResponse(Call<Result<TrainingPlan>> call, Response<Result<TrainingPlan>> response) {
                    pendingCount[0]--;

                    if (response.isSuccessful() && response.body() != null
                            && response.body().getCode() == 200 && response.body().getData() != null) {
                        TrainingPlan plan = response.body().getData();
                        record.setSportName(plan.getPlanName());
                        Log.d("CheckInRecords", "Found plan: " + plan.getPlanName());
                    } else {
                        Log.d("CheckInRecords", "Plan not found for eventID: " + record.getEventID());
                    }

                    if (pendingCount[0] == 0) {
                        adapter.updateData(records);
                        updateStatistics();
                        showRecords();
                    }
                }

                @Override
                public void onFailure(Call<Result<TrainingPlan>> call, Throwable t) {
                    pendingCount[0]--;
                    Log.e("CheckInRecords", "Failed to fetch plan", t);

                    if (pendingCount[0] == 0) {
                        adapter.updateData(records);
                        updateStatistics();
                        showRecords();
                    }
                }
            });
        }
    }

    private void updateStatistics() {
        if (records.isEmpty()) {
            tvTotalDays.setText("0");
            tvTotalDuration.setText("0");
            tvTotalCalorie.setText("0");
            return;
        }

        int totalDays = (int) records.stream()
                .map(ExerciseRecord::getSportsDate)
                .distinct()
                .count();

        int totalDuration = records.stream()
                .mapToInt(r -> r.getExerciseDuration() != null ? r.getExerciseDuration() : 0)
                .sum();

        int totalCalorie = records.stream()
                .mapToInt(r -> r.getCalorie() != null ? r.getCalorie() : 0)
                .sum();

        tvTotalDays.setText(String.valueOf(totalDays));
        tvTotalDuration.setText(String.valueOf(totalDuration));
        tvTotalCalorie.setText(String.valueOf(totalCalorie));
    }

    private void showRecords() {
        recyclerView.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        recyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
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