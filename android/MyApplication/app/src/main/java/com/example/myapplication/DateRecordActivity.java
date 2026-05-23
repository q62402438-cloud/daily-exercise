package com.example.myapplication;

import android.app.DatePickerDialog;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.ExerciseRecord;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateRecordActivity extends AppCompatActivity {

    private TextView tvStartDate;
    private TextView tvEndDate;
    private Button btnSearch;
    private RecyclerView recyclerView;
    private ExerciseRecordAdapter adapter;
    private List<ExerciseRecord> records = new ArrayList<>();
    private View emptyState;
    private TextView tvTotalDays;
    private TextView tvTotalDuration;
    private TextView tvTotalCalorie;

    private String startDateStr = "";
    private String endDateStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_date_record);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
    }

    private void initViews() {
        View backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        btnSearch = findViewById(R.id.btn_search);
        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.empty_state);
        tvTotalDays = findViewById(R.id.tv_total_days);
        tvTotalDuration = findViewById(R.id.tv_total_duration);
        tvTotalCalorie = findViewById(R.id.tv_total_calorie);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ExerciseRecordAdapter(records);
        recyclerView.setAdapter(adapter);

        tvStartDate.setOnClickListener(v -> showDatePicker(true));
        tvEndDate.setOnClickListener(v -> showDatePicker(false));

        btnSearch.setOnClickListener(v -> searchRecords());
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            if (isStartDate) {
                tvStartDate.setText(date);
                startDateStr = date;
            } else {
                tvEndDate.setText(date);
                endDateStr = date;
            }
        }, year, month, day);
        dialog.show();
    }

    private void searchRecords() {
        if (startDateStr.isEmpty()) {
            Toast.makeText(this, "请选择开始日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDateStr.isEmpty()) {
            Toast.makeText(this, "请选择结束日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (startDateStr.compareTo(endDateStr) > 0) {
            Toast.makeText(this, "开始日期不能晚于结束日期", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        ExerciseRecord request = new ExerciseRecord();
        request.setUserID(user.getUserID());
        request.setStartDate(startDateStr);
        request.setEndDate(endDateStr);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<ExerciseRecord>>> call = apiService.getExerciseRecordsByDateRange(request);

        call.enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<ExerciseRecord>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        records = result.getData();
                        fetchMissingPlanNames(records);
                    } else {
                        records.clear();
                        adapter.updateData(records);
                        showEmptyState();
                        updateStatistics();
                    }
                } else {
                    records.clear();
                    adapter.updateData(records);
                    showEmptyState();
                    updateStatistics();
                }
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                Log.e("DateRecord", "查询失败", t);
                Toast.makeText(DateRecordActivity.this, "查询失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                records.clear();
                adapter.updateData(records);
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
                        Log.d("DateRecord", "Found plan: " + plan.getPlanName());
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
                    Log.e("DateRecord", "Failed to fetch plan", t);

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

        long totalDays = records.stream()
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