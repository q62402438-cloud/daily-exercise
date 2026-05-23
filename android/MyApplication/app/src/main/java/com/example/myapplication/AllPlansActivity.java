package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllPlansActivity extends AppCompatActivity {
    private static final String TAG = "AllPlansActivity";

    private RecyclerView rvPlans;
    private PlanAdapter planAdapter;
    private ApiService apiService;
    private List<TrainingPlan> allPlans = new ArrayList<>();
    private EditText etSearch;

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
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        loadPlanList();
        setupBottomNavigation();
        setupClickListeners();
    }

    private void initViews() {
        rvPlans = findViewById(R.id.rv_plans);
        etSearch = findViewById(R.id.et_search);

        if (rvPlans != null) {
            rvPlans.setLayoutManager(new LinearLayoutManager(this));
            planAdapter = new PlanAdapter(new ArrayList<>(), this::onPlanClick);
            rvPlans.setAdapter(planAdapter);
        }

        Button searchBtn = findViewById(R.id.btn_search);
        if (searchBtn != null) {
            searchBtn.setOnClickListener(v -> searchPlans());
        }

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                searchPlans();
                return true;
            });
        }
    }

    private void searchPlans() {
        String keyword = etSearch != null ? etSearch.getText().toString().toLowerCase().trim() : "";

        if (keyword.isEmpty()) {
            if (planAdapter != null) {
                planAdapter.setPlanList(mapPlans(allPlans));
            }
            return;
        }

        List<TrainingPlan> filtered = new ArrayList<>();
        for (TrainingPlan plan : allPlans) {
            String planName = plan.getPlanName() != null ? plan.getPlanName().toLowerCase() : "";
            String sportName = plan.getSportName() != null ? plan.getSportName().toLowerCase() : "";
            if (planName.contains(keyword) || sportName.contains(keyword)) {
                filtered.add(plan);
            }
        }

        if (planAdapter != null) {
            planAdapter.setPlanList(mapPlans(filtered));
        }

        if (filtered.isEmpty()) {
            Toast.makeText(this, "未找到匹配的计划", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPlanList() {
        apiService.getPublishedTrainingPlans(new java.util.HashMap<>()).enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    allPlans = response.body().getData();
                    if (planAdapter != null) {
                        planAdapter.setPlanList(mapPlans(allPlans));
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                Log.e(TAG, "load published plans failed", t);
            }
        });
    }

    private List<Plan> mapPlans(List<TrainingPlan> trainingPlans) {
        List<Plan> plans = new ArrayList<>();
        for (TrainingPlan trainingPlan : trainingPlans) {
            Integer planType = trainingPlan.getPlanType();
            boolean isPublic = planType != null && planType >= 10;
            plans.add(new Plan(
                    String.valueOf(trainingPlan.getPlanID()),
                    trainingPlan.getPlanName() == null ? "" : trainingPlan.getPlanName(),
                    trainingPlan.getSportName() == null ? "" : trainingPlan.getSportName(),
                    trainingPlan.getStartTime() == null ? "" : trainingPlan.getStartTime(),
                    trainingPlan.getEndTime() == null ? "" : trainingPlan.getEndTime(),
                    trainingPlan.getExerciseAmount() == null ? "" : trainingPlan.getExerciseAmount(),
                    trainingPlan.getDailyCalorie() == null ? "" : trainingPlan.getDailyCalorie(),
                    isPublic
            ));
        }
        return plans;
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