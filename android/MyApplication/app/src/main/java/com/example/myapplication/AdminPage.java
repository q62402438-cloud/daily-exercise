package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.AdminUser;

public class AdminPage extends AppCompatActivity {

    private TextView tvAdminId, tvAdminName, tvAdminRole;
    private LinearLayout llPlanReview, llPostReview;
    private Button btnLogout;
    private AdminUser adminUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        tvAdminId = findViewById(R.id.tv_admin_id);
        tvAdminName = findViewById(R.id.tv_admin_name);
        tvAdminRole = findViewById(R.id.tv_admin_role);
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
            Intent intent = new Intent(AdminPage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}