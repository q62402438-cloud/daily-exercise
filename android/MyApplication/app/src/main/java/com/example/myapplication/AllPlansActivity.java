package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AllPlansActivity extends AppCompatActivity {

    private RecyclerView rvPlans;
    private PlanAdapter planAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_plans);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        loadPlanList();
        setupBottomNavigation();
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
                "2025-01-01", "2025-01-30", "5", "300", true));

        plans.add(new Plan("plan_2", "清晨瑜伽放松计划", "瑜伽",
                "2025-02-01", "2025-02-28", "30", "150", false));

        plans.add(new Plan("plan_3", "减脂训练计划", "力量训练",
                "2025-03-01", "2025-03-31", "45", "400", true));

        plans.add(new Plan("plan_4", "游泳入门计划", "游泳",
                "2025-04-01", "2025-04-30", "60", "500", true));

        plans.add(new Plan("plan_5", "骑行挑战计划", "骑行",
                "2025-05-01", "2025-05-31", "15", "450", false));

        plans.add(new Plan("plan_6", "HIIT燃脂计划", "高强度间歇",
                "2025-06-01", "2025-06-30", "20", "350", true));

        plans.add(new Plan("plan_7", "核心训练计划", "核心力量",
                "2025-07-01", "2025-07-31", "30", "250", true));

        plans.add(new Plan("plan_8", "柔韧性提升计划", "拉伸",
                "2025-08-01", "2025-08-31", "20", "100", false));

        if (planAdapter != null) {
            planAdapter.setPlanList(plans);
        }
    }

    private void onPlanClick(Plan plan) {
        Intent intent = new Intent(AllPlansActivity.this, PlanDetailActivity.class);
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

    private void setupBottomNavigation() {
        RelativeLayout homeTab = findViewById(R.id.tab_home);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                Intent intent = new Intent(AllPlansActivity.this, HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout sportTab = findViewById(R.id.tab_sport);
        if (sportTab != null) {
            sportTab.setOnClickListener(v -> {
                Intent intent = new Intent(AllPlansActivity.this, SportPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout forumTab = findViewById(R.id.tab_forum);
        if (forumTab != null) {
            forumTab.setOnClickListener(v -> {
                Intent intent = new Intent(AllPlansActivity.this, ForumActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout profileTab = findViewById(R.id.tab_profile);
        if (profileTab != null) {
            profileTab.setOnClickListener(v -> {
                Intent intent = new Intent(AllPlansActivity.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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