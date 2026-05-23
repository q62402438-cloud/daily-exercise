package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity {

    private ApiService apiService;
    private SessionManager sessionManager;
    private RecyclerView recyclerRecommendedPlans;
    private RecommendedPlanAdapter recommendedPlanAdapter;
    private List<TrainingPlan> recommendedPlanList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        sessionManager = new SessionManager(this);
        initRecyclerView();
        highlightCurrentTab("home");
        initClickListeners();
        loadUserData();
        loadFeaturedPost();
        loadRecommendedPlans();
    }

    private void highlightCurrentTab(String currentTab) {
        ImageView homeIcon = findViewById(R.id.icon_home);
        ImageView sportIcon = findViewById(R.id.icon_sport);
        ImageView forumIcon = findViewById(R.id.icon_forum);
        ImageView profileIcon = findViewById(R.id.icon_profile);

        if (currentTab.equals("home")) {
            if (homeIcon != null) homeIcon.setImageResource(R.drawable.ic_home_active);
        } else {
            if (homeIcon != null) homeIcon.setImageResource(R.drawable.ic_home);
        }

        if (currentTab.equals("sport")) {
            if (sportIcon != null) sportIcon.setImageResource(R.drawable.ic_sports_active);
        } else {
            if (sportIcon != null) sportIcon.setImageResource(R.drawable.ic_sports);
        }

        if (currentTab.equals("forum")) {
            if (forumIcon != null) forumIcon.setImageResource(R.drawable.ic_forum_active);
        } else {
            if (forumIcon != null) forumIcon.setImageResource(R.drawable.ic_forum);
        }

        if (currentTab.equals("profile")) {
            if (profileIcon != null) profileIcon.setImageResource(R.drawable.ic_profile_active);
        } else {
            if (profileIcon != null) profileIcon.setImageResource(R.drawable.ic_profile);
        }
    }

    private void initClickListeners() {
        ImageView userAvatar = findViewById(R.id.user_avatar);
        if (userAvatar != null) {
            userAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, ChangeUserInfo.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        TextView viewAllPlans = findViewById(R.id.view_all_plans);
        if (viewAllPlans != null) {
            viewAllPlans.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, AllPlansActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        RelativeLayout homeTab = findViewById(R.id.tab_home);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
            });
        }

        RelativeLayout sportTab = findViewById(R.id.tab_sport);
        if (sportTab != null) {
            sportTab.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, SportPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout forumTab = findViewById(R.id.tab_forum);
        if (forumTab != null) {
            forumTab.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, ForumActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout profileTab = findViewById(R.id.tab_profile);
        if (profileTab != null) {
            profileTab.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, ProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void initRecyclerView() {
        recyclerRecommendedPlans = findViewById(R.id.recycler_recommended_plans);
        if (recyclerRecommendedPlans != null) {
            recyclerRecommendedPlans.setLayoutManager(new LinearLayoutManager(this));
            recommendedPlanAdapter = new RecommendedPlanAdapter(recommendedPlanList, this::onPlanClick);
            recyclerRecommendedPlans.setAdapter(recommendedPlanAdapter);
        }
    }

    private void loadUserData() {
        Integer userId = sessionManager.getUserId();
        if (userId != null) {
            loadCheckInDays(userId);
        }
    }

    private void loadCheckInDays(Integer userId) {
        User user = new User();
        user.setUserID(userId);
        apiService.getExerciseRecordsByUser(user).enqueue(new Callback<Result<List<ExerciseRecord>>>() {
            @Override
            public void onResponse(Call<Result<List<ExerciseRecord>>> call, Response<Result<List<ExerciseRecord>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    List<ExerciseRecord> records = response.body().getData();
                    Set<String> checkInDays = new HashSet<>();
                    for (ExerciseRecord record : records) {
                        String sportsDate = record.getSportsDate();
                        if (sportsDate != null && !sportsDate.isEmpty()) {
                            checkInDays.add(sportsDate);
                        }
                    }
                    TextView stickDays = findViewById(R.id.stick_days);
                    if (stickDays != null) {
                        stickDays.setText(checkInDays.size() + "天");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<ExerciseRecord>>> call, Throwable t) {
                // 加载失败时不做特殊处理
            }
        });
    }

    private void loadFeaturedPost() {
        Map<String, Integer> body = new HashMap<>();
        body.put("status", 1); // 已审核通过的帖子
        body.put("page", 0);
        body.put("size", 1);

        apiService.getPosts(body).enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null && !response.body().getData().isEmpty()) {
                    PostEntity featuredPost = response.body().getData().get(0);
                    updateFeaturedPostCard(featuredPost);
                }
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
                // 加载失败时不做特殊处理
            }
        });
    }

    private void updateFeaturedPostCard(PostEntity post) {
        View featuredPostCard = findViewById(R.id.featured_post_card);
        if (featuredPostCard != null) {
            ((TextView) featuredPostCard.findViewById(R.id.tv_post_title)).setText(post.getTitle() != null ? post.getTitle() : "优秀主题帖");
            ((TextView) featuredPostCard.findViewById(R.id.tv_view_count)).setText((post.getViewCount() != null ? post.getViewCount() : 0) + "人浏览");

            featuredPostCard.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, PostDetailActivity.class);
                intent.putExtra("post_id", post.getPostID() != null ? String.valueOf(post.getPostID()) : "featured_post");
                intent.putExtra("author_name", post.getAuthorName() != null ? post.getAuthorName() : "用户");
                intent.putExtra("post_time", post.getPublishTime() != null ? post.getPublishTime() : "");
                intent.putExtra("post_title", post.getTitle() != null ? post.getTitle() : "");
                intent.putExtra("post_content", post.getContent() != null ? post.getContent() : "");
                intent.putExtra("view_count", post.getViewCount() != null ? String.valueOf(post.getViewCount()) : "0");
                intent.putExtra("like_count", post.getLikeCount() != null ? String.valueOf(post.getLikeCount()) : "0");
                intent.putExtra("comment_count", post.getCommentCount() != null ? String.valueOf(post.getCommentCount()) : "0");
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }
    }

    private void loadRecommendedPlans() {
        Map<String, Object> body = new HashMap<>();
        body.put("status", 2); // 审核通过的计划
        body.put("page", 0);
        body.put("size", 5);

        apiService.getPublishedTrainingPlans(body).enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    recommendedPlanList.clear();
                    recommendedPlanList.addAll(response.body().getData());
                    recommendedPlanAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                // 加载失败时不做特殊处理
            }
        });
    }

    private void onPlanClick(TrainingPlan plan) {
        Intent intent = new Intent(HomePage.this, PlanDetailActivity.class);
        intent.putExtra("plan_id", String.valueOf(plan.getPlanID()));
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public static class RecommendedPlanAdapter extends RecyclerView.Adapter<RecommendedPlanAdapter.PlanViewHolder> {
        private List<TrainingPlan> planList;
        private OnPlanClickListener listener;

        public interface OnPlanClickListener {
            void onPlanClick(TrainingPlan plan);
        }

        public RecommendedPlanAdapter(List<TrainingPlan> planList, OnPlanClickListener listener) {
            this.planList = planList;
            this.listener = listener;
        }

        @androidx.annotation.NonNull
        @Override
        public PlanViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_plan, parent, false);
            return new PlanViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@androidx.annotation.NonNull PlanViewHolder holder, int position) {
            TrainingPlan plan = planList.get(position);
            holder.bind(plan);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onPlanClick(plan);
                }
            });
        }

        @Override
        public int getItemCount() {
            return planList.size();
        }

        static class PlanViewHolder extends RecyclerView.ViewHolder {
            TextView tvPlanName;
            TextView tvPlanInfo;
            TextView tvStartButton;

            public PlanViewHolder(@androidx.annotation.NonNull View itemView) {
                super(itemView);
                tvPlanName = itemView.findViewById(R.id.tv_plan_name);
                tvPlanInfo = itemView.findViewById(R.id.tv_plan_info);
                tvStartButton = itemView.findViewById(R.id.tv_start_button);
            }

            public void bind(TrainingPlan plan) {
                if (tvPlanName != null) {
                    tvPlanName.setText(plan.getPlanName() != null ? plan.getPlanName() : "未命名计划");
                }
                if (tvPlanInfo != null) {
                    String sportType = plan.getSportName() != null ? plan.getSportName() : "";
                    String exerciseAmount = plan.getExerciseAmount() != null ? plan.getExerciseAmount() : "";
                    tvPlanInfo.setText(!sportType.isEmpty() && !exerciseAmount.isEmpty() ? sportType + " · " + exerciseAmount + "分钟" : !sportType.isEmpty() ? sportType : !exerciseAmount.isEmpty() ? exerciseAmount + "分钟" : "推荐计划");
                }
            }
        }
    }
}
