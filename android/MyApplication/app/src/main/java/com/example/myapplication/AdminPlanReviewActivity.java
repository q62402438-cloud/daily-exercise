package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminPlanReviewActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private PlanAdapter planAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_plan_review);

        initViews();
        loadPlanList();
        setupClickListeners();
    }

    private void initViews() {
        rvPlans = findViewById(R.id.rv_plans);
        if (rvPlans != null) {
            rvPlans.setLayoutManager(new LinearLayoutManager(this));
            planAdapter = new PlanAdapter(new ArrayList<>(), this::onPlanClick);
            rvPlans.setAdapter(planAdapter);
        }
    }

    private void loadPlanList() {
        List<Plan> plans = new ArrayList<>();

        plans.add(new Plan("plan_1", "初级跑步计划", "跑步",
                "2025-01-01", "2025-01-30", "5", "300", false));

        plans.add(new Plan("plan_2", "清晨瑜伽放松计划", "瑜伽",
                "2025-02-01", "2025-02-28", "30", "150", false));

        plans.add(new Plan("plan_3", "减脂训练计划", "力量训练",
                "2025-03-01", "2025-03-31", "45", "400", false));

        plans.add(new Plan("plan_4", "游泳入门计划", "游泳",
                "2025-04-01", "2025-04-30", "60", "500", false));

        plans.add(new Plan("plan_5", "骑行挑战计划", "骑行",
                "2025-05-01", "2025-05-31", "15", "450", false));

        if (planAdapter != null) {
            planAdapter.setPlanList(plans);
        }
    }

    private void onPlanClick(Plan plan) {
        Intent intent = new Intent(AdminPlanReviewActivity.this, AdminPlanReviewDetailActivity.class);
        intent.putExtra("plan_id", plan.getPlanId());
        intent.putExtra("plan_name", plan.getPlanName());
        intent.putExtra("sport_type", plan.getSportType());
        intent.putExtra("start_date", plan.getStartDate());
        intent.putExtra("end_date", plan.getEndDate());
        intent.putExtra("daily_exercise", plan.getDailyExercise());
        intent.putExtra("daily_calorie", plan.getDailyCalorie());
        intent.putExtra("is_public", plan.isPublic());
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
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}