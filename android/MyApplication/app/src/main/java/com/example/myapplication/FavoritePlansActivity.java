package com.example.myapplication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.FavoriteEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritePlansActivity extends AppCompatActivity {
    private FavoritePlanAdapter favoritePlanAdapter;
    private List<Plan> planList = new ArrayList<>();
    private List<FavoriteEntity> favoriteEntities = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_plans);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        setupRecyclerView();
        setupClickListeners();
        loadFavoritePlans();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_favorite_plans);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritePlanAdapter = new FavoritePlanAdapter(planList, new FavoritePlanAdapter.OnFavoriteActionListener() {
            @Override
            public void onPlanClick(Plan plan) {
                Intent intent = new Intent(FavoritePlansActivity.this, PlanDetailActivity.class);
                intent.putExtra("plan_id", plan.getPlanId());
                intent.putExtra("plan_name", plan.getPlanName());
                intent.putExtra("sport_type", plan.getSportType());
                intent.putExtra("start_date", plan.getStartDate());
                intent.putExtra("end_date", plan.getEndDate());
                intent.putExtra("daily_exercise", plan.getDailyExercise());
                intent.putExtra("daily_calorie", plan.getDailyCalorie());
                intent.putExtra("is_public", plan.isPublic());
                startActivity(intent);
            }

            @Override
            public void onUnfavoriteClick(Plan plan, int position) {
                showUnfavoriteConfirmDialog(plan, position);
            }
        });
        recyclerView.setAdapter(favoritePlanAdapter);
    }

    private void setupClickListeners() {
        RelativeLayout backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button btnClearAll = findViewById(R.id.btn_clear_all);
        if (btnClearAll != null) {
            btnClearAll.setOnClickListener(v -> showClearAllConfirmDialog());
        }
    }

    private void loadFavoritePlans() {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            return;
        }
        User user = new User();
        user.setUserID(userId);
        apiService.getFavoritesByUser(user).enqueue(new Callback<Result<List<FavoriteEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<FavoriteEntity>>> call, Response<Result<List<FavoriteEntity>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    favoriteEntities.clear();
                    List<FavoriteEntity> favoritePlans = new ArrayList<>();
                    for (FavoriteEntity favoriteEntity : response.body().getData()) {
                        if (favoriteEntity.getTargetType() != null && favoriteEntity.getTargetType() == 1) {
                            favoriteEntities.add(favoriteEntity);
                            favoritePlans.add(favoriteEntity);
                        }
                    }
                    loadPlanDetails(favoritePlans);
                } else {
                    findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Result<List<FavoriteEntity>>> call, Throwable t) {
                findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadPlanDetails(List<FavoriteEntity> favoritePlans) {
        if (favoritePlans.isEmpty()) {
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            planList.clear();
            favoritePlanAdapter.notifyDataSetChanged();
            return;
        }
        planList.clear();
        final int[] remain = {favoritePlans.size()};
        for (FavoriteEntity favorite : favoritePlans) {
            TrainingPlan request = new TrainingPlan();
            request.setPlanID(favorite.getTargetID());
            apiService.getTrainingPlanById(request).enqueue(new Callback<Result<TrainingPlan>>() {
                @Override
                public void onResponse(Call<Result<TrainingPlan>> call, Response<Result<TrainingPlan>> response) {
                    remain[0]--;
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getCode() == 200 && response.body().getData() != null) {
                        TrainingPlan p = response.body().getData();
                        planList.add(new Plan(
                                String.valueOf(p.getPlanID()),
                                p.getPlanName() == null ? "" : p.getPlanName(),
                                p.getSportName() == null ? "" : p.getSportName(),
                                p.getStartTime() == null ? "" : p.getStartTime(),
                                p.getEndTime() == null ? "" : p.getEndTime(),
                                p.getExerciseAmount() == null ? "" : p.getExerciseAmount(),
                                p.getDailyCalorie() == null ? "" : p.getDailyCalorie(),
                                p.getPlanType() != null && p.getPlanType() >= 10
                        ));
                    }
                    maybeDone(remain[0]);
                }

                @Override
                public void onFailure(Call<Result<TrainingPlan>> call, Throwable t) {
                    remain[0]--;
                    maybeDone(remain[0]);
                }
            });
        }
    }

    private void maybeDone(int remain) {
        if (remain == 0) {
            findViewById(R.id.tv_empty).setVisibility(planList.isEmpty() ? View.VISIBLE : View.GONE);
            favoritePlanAdapter.notifyDataSetChanged();
        }
    }

    private void showUnfavoriteConfirmDialog(Plan plan, int position) {
        new AlertDialog.Builder(this)
                .setTitle("取消收藏")
                .setMessage("确定要取消收藏这个训练计划吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    unfavoritePlan(plan, position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void unfavoritePlan(Plan plan, int position) {
        if (position >= 0 && position < favoriteEntities.size()) {
            FavoriteEntity favorite = favoriteEntities.get(position);
            if (favorite.getFavoriteID() != null) {
                apiService.deleteFavorite(favorite).enqueue(new Callback<Result<String>>() {
                    @Override
                    public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getCode() == 200) {
                            Toast.makeText(FavoritePlansActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                            favoriteEntities.remove(position);
                            favoritePlanAdapter.removeItem(position);

                            if (planList.isEmpty()) {
                                findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(FavoritePlansActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Result<String>> call, Throwable t) {
                        Toast.makeText(FavoritePlansActivity.this, "取消收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void showClearAllConfirmDialog() {
        if (planList.isEmpty()) {
            Toast.makeText(this, "暂无收藏可清空", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("清空所有收藏")
                .setMessage("确定要清空所有收藏的训练计划吗？此操作不可恢复！")
                .setPositiveButton("确认清空", (dialog, which) -> {
                    clearAllFavorites();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void clearAllFavorites() {
        if (favoriteEntities.isEmpty()) {
            return;
        }

        final int[] remain = {favoriteEntities.size()};
        final boolean[] allSuccess = {true};

        for (FavoriteEntity favorite : favoriteEntities) {
            if (favorite.getFavoriteID() != null) {
                apiService.deleteFavorite(favorite).enqueue(new Callback<Result<String>>() {
                    @Override
                    public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                        remain[0]--;
                        if (!(response.isSuccessful() && response.body() != null
                                && response.body().getCode() == 200)) {
                            allSuccess[0] = false;
                        }
                        if (remain[0] == 0) {
                            if (allSuccess[0]) {
                                Toast.makeText(FavoritePlansActivity.this, "已清空所有收藏", Toast.LENGTH_SHORT).show();
                                planList.clear();
                                favoriteEntities.clear();
                                favoritePlanAdapter.notifyDataSetChanged();
                                findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(FavoritePlansActivity.this, "部分收藏清空失败", Toast.LENGTH_SHORT).show();
                                loadFavoritePlans();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Result<String>> call, Throwable t) {
                        remain[0]--;
                        allSuccess[0] = false;
                        if (remain[0] == 0) {
                            Toast.makeText(FavoritePlansActivity.this, "清空收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                remain[0]--;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
