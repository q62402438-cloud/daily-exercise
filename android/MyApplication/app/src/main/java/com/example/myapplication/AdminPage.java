package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.AdminUser;
import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPage extends AppCompatActivity {

    private TextView tvAdminId, tvAdminName, tvAdminRole;
    private TextView tvPlanCount, tvPostCount;
    private LinearLayout llPlanReview, llPostReview;
    private Button btnLogout;
    private AdminUser adminUser;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        initView();
        initData();
        initListener();
        loadPendingCounts();
    }

    private void initView() {
        tvAdminId = findViewById(R.id.tv_admin_id);
        tvAdminName = findViewById(R.id.tv_admin_name);
        tvAdminRole = findViewById(R.id.tv_admin_role);
        tvPlanCount = findViewById(R.id.tv_plan_count);
        tvPostCount = findViewById(R.id.tv_post_count);
        llPlanReview = findViewById(R.id.ll_plan_review);
        llPostReview = findViewById(R.id.ll_post_review);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("admin_data")) {
            adminUser = (AdminUser) intent.getSerializableExtra("admin_data");
            if (adminUser != null) {
                tvAdminId.setText(adminUser.getAdminId());
                tvAdminName.setText(adminUser.getAdminName());
                tvAdminRole.setText(adminUser.getRole());
            }
        }
    }

    private void loadPendingCounts() {
        loadPendingPlanCount();
        loadPendingPostCount();
    }

    private void loadPendingPlanCount() {
        Call<Result<List<TrainingPlan>>> call = apiService.getPendingTrainingPlans();
        call.enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<TrainingPlan>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        int count = result.getData().size();
                        tvPlanCount.setText("待审核: " + count + "条");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
            }
        });
    }

    private void loadPendingPostCount() {
        Call<Result<List<PostEntity>>> call = apiService.getPendingPosts();
        call.enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<PostEntity>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        int count = result.getData().size();
                        tvPostCount.setText("待审核: " + count + "条");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
            }
        });
    }

    private void initListener() {
        llPlanReview.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, AdminPlanReviewActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        llPostReview.setOnClickListener(v -> {
            Intent intent = new Intent(AdminPage.this, AdminPostReviewActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        btnLogout.setOnClickListener(v -> {
            new SessionManager(AdminPage.this).clear();
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPendingCounts();
    }
}