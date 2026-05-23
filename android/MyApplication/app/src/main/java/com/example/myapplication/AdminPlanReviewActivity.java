package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPlanReviewActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private TrainingPlanAdapter planAdapter;
    private ApiService apiService;
    private List<TrainingPlan> planList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_plan_review);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        initViews();
        loadPlanList();
        setupClickListeners();
    }

    private void initViews() {
        rvPlans = findViewById(R.id.rv_plans);
        if (rvPlans != null) {
            rvPlans.setLayoutManager(new LinearLayoutManager(this));
            planAdapter = new TrainingPlanAdapter(new ArrayList<>(), this::onPlanClick);
            rvPlans.setAdapter(planAdapter);
        }
    }

    private void loadPlanList() {
        Call<Result<List<TrainingPlan>>> call = apiService.getPendingTrainingPlans();
        call.enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<TrainingPlan>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        planList = result.getData();
                        planAdapter.setPlanList(planList);
                    } else {
                        String message = result.getMessage() != null ? result.getMessage() : "未知错误";
                        Toast.makeText(AdminPlanReviewActivity.this, "加载失败: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminPlanReviewActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                Toast.makeText(AdminPlanReviewActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onPlanClick(TrainingPlan plan) {
        Intent intent = new Intent(AdminPlanReviewActivity.this, AdminPlanReviewDetailActivity.class);
        Integer planId = plan.getPlanID();
        if (planId != null) {
            intent.putExtra("plan_id", planId);
        } else {
            intent.putExtra("plan_id", -1);
        }
        intent.putExtra("plan_name", plan.getPlanName());
        intent.putExtra("sport_type", plan.getSportName());
        intent.putExtra("start_date", plan.getStartTime());
        intent.putExtra("end_date", plan.getEndTime());
        intent.putExtra("daily_exercise", plan.getExerciseAmount());
        intent.putExtra("user_id", plan.getUserID());
        intent.putExtra("plan_detail", plan.getDetail());
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlanList(); // 刷新数据
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}