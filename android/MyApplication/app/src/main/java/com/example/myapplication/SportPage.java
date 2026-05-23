package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SportPage extends AppCompatActivity {
    private static final String TAG = "SportPage";

    private RecyclerView rvPlans;
    private PlanAdapter planAdapter;
    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sport_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sessionManager = new SessionManager(this);
        highlightCurrentTab("sport");
        initViews();
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        loadPlanList();
        checkTodayCheckIn();
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

    private void checkTodayCheckIn() {
        Integer userId = sessionManager.getUserId();
        if (userId == null) {
            return;
        }
        User user = new User();
        user.setUserID(userId);
        apiService.getExerciseRecordsByUser(user).enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    List<ExerciseRecord> records = response.body().getData();
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    boolean hasCheckIn = false;
                    for (ExerciseRecord record : records) {
                        if (today.equals(record.getSportsDate())) {
                            hasCheckIn = true;
                            break;
                        }
                    }
                    TextView tvTodayStatus = findViewById(R.id.tv_today_status);
                    if (tvTodayStatus != null) {
                        tvTodayStatus.setText(hasCheckIn ? "今天已经运动过啦" : "今天还没运动哦");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                // 加载失败时不做特殊处理
            }
        });
    }

    private void loadPlanList() {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            return;
        }
        User user = new User();
        user.setUserID(userId);
        apiService.getTrainingPlansByUser(user).enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    if (planAdapter != null) {
                        planAdapter.setPlanList(mapPlans(response.body().getData()));
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                Log.e(TAG, "load plans failed", t);
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
                    safe(trainingPlan.getPlanName()),
                    safe(trainingPlan.getSportName()),
                    safe(trainingPlan.getStartTime()),
                    safe(trainingPlan.getEndTime()),
                    safe(trainingPlan.getExerciseAmount()),
                    safe(trainingPlan.getDailyCalorie()),
                    isPublic
            ));
        }
        return plans;
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private void onPlanClick(Plan plan) {
        Intent intent = new Intent(SportPage.this, PlanDetailActivity.class);
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

    private void highlightCurrentTab(String currentTab) {
        ImageView homeIcon = findViewById(R.id.icon_home);
        ImageView sportIcon = findViewById(R.id.icon_sport);
        ImageView forumIcon = findViewById(R.id.icon_forum);
        ImageView profileIcon = findViewById(R.id.icon_profile);

        int activeColor = android.graphics.Color.parseColor("#2E7D32");
        int inactiveColor = android.graphics.Color.parseColor("#666666");

        if (homeIcon != null) {
            homeIcon.setColorFilter(currentTab.equals("home") ? activeColor : inactiveColor);
        }

        if (sportIcon != null) {
            sportIcon.setColorFilter(currentTab.equals("sport") ? activeColor : inactiveColor);
        }

        if (forumIcon != null) {
            forumIcon.setColorFilter(currentTab.equals("forum") ? activeColor : inactiveColor);
        }

        if (profileIcon != null) {
            profileIcon.setColorFilter(currentTab.equals("profile") ? activeColor : inactiveColor);
        }
    }

    private void setupClickListeners() {
        LinearLayout startExercise = findViewById(R.id.btn_start_exercise);
        if (startExercise != null) {
            startExercise.setOnClickListener(v -> {
                Intent intent = new Intent(SportPage.this, ExerciseCheckInActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout checkInRecords = findViewById(R.id.btn_check_in_records);
        if (checkInRecords != null) {
            checkInRecords.setOnClickListener(v -> {
                Intent intent = new Intent(SportPage.this, CheckInRecordsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout createPlan = findViewById(R.id.create_plan);
        if (createPlan != null) {
            createPlan.setOnClickListener(v -> {
                Intent intent = new Intent(SportPage.this, CreateExercisePlanActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void setupBottomNavigation() {
        RelativeLayout homeTab = findViewById(R.id.tab_home);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                Intent intent = new Intent(SportPage.this, HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout sportTab = findViewById(R.id.tab_sport);
        if (sportTab != null) {
            sportTab.setOnClickListener(v -> {
            });
        }

        RelativeLayout forumTab = findViewById(R.id.tab_forum);
        if (forumTab != null) {
            forumTab.setOnClickListener(v -> {
                Intent intent = new Intent(SportPage.this, ForumActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout profileTab = findViewById(R.id.tab_profile);
        if (profileTab != null) {
            profileTab.setOnClickListener(v -> {
                Intent intent = new Intent(SportPage.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlanList();
        checkTodayCheckIn();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}