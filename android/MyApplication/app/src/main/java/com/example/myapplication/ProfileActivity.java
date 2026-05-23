package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    private TextView tvUserName;
    private TextView tvUserInfo;
    private TextView tvUserEmail;
    private TextView tvUserGender;
    private TextView tvUserBirthday;
    private TextView tvPostCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        highlightCurrentTab("profile");
        setupBottomNavigation();
        setupClickListeners();
        loadUserInfo();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserInfo = findViewById(R.id.tv_user_info);
        tvUserEmail = findViewById(R.id.user_email);
        tvUserGender = findViewById(R.id.user_gender);
        tvUserBirthday = findViewById(R.id.user_birthday);
        tvPostCount = findViewById(R.id.post_count);
        tvUserName.setText("加载中...");
        tvUserInfo.setText("");
        tvPostCount.setText("0篇");
    }

    private void loadUserInfo() {
        User user = getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<OrdinaryUser>> call = apiService.getUserInfo(user);

        call.enqueue(new Callback<Result<OrdinaryUser>>() {
            @Override
            public void onResponse(Call<Result<OrdinaryUser>> call, Response<Result<OrdinaryUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<OrdinaryUser> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        OrdinaryUser userData = result.getData();
                        displayUserInfo(userData);
                        loadPostCount();
                    } else {
                        displayDefaultInfo();
                    }
                } else {
                    displayDefaultInfo();
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                Log.e(TAG, "加载用户信息失败", t);
                displayDefaultInfo();
            }
        });
    }

    private void displayUserInfo(OrdinaryUser user) {
        String userName = user.getUserName() != null ? user.getUserName() : "运动达人";
        tvUserName.setText(userName);

        String email = user.getUserMailbox() != null && !user.getUserMailbox().isEmpty()
                ? user.getUserMailbox() : "未设置";
        tvUserEmail.setText(email);

        String gender = user.getGender() != null && !user.getGender().isEmpty()
                ? user.getGender() : "未设置";
        tvUserGender.setText(gender);

        String birthday = user.getBirthday() != null && !user.getBirthday().isEmpty()
                ? formatBirthday(user.getBirthday()) : "未设置";
        tvUserBirthday.setText(birthday);

        StringBuilder infoBuilder = new StringBuilder();
        if (user.getGender() != null && !user.getGender().isEmpty()) {
            infoBuilder.append(user.getGender());
        }
        if (user.getAge() != null) {
            if (infoBuilder.length() > 0) infoBuilder.append(" | ");
            infoBuilder.append(user.getAge()).append("岁");
        }
        if (user.getWeight() != null) {
            if (infoBuilder.length() > 0) infoBuilder.append(" | ");
            infoBuilder.append(user.getWeight()).append("kg");
        }
        tvUserInfo.setText(infoBuilder.length() > 0 ? infoBuilder.toString() : "完善个人信息");
    }

    private void loadPostCount() {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<PostEntity>>> call = apiService.getPostsByAuthor(userId);

        call.enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<PostEntity>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        int count = result.getData().size();
                        if (tvPostCount != null) {
                            tvPostCount.setText(count + "篇");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
                Log.e(TAG, "加载帖子数量失败", t);
            }
        });
    }

    private String formatBirthday(String birthday) {
        if (birthday == null || birthday.isEmpty()) {
            return "";
        }
        try {
            if (birthday.contains("T")) {
                birthday = birthday.split("T")[0];
            }
            String[] parts = birthday.split("-");
            if (parts.length == 3) {
                return parts[0] + "年" + parts[1] + "月" + parts[2] + "日";
            }
        } catch (Exception e) {
            Log.w(TAG, "格式化生日失败: " + birthday);
        }
        return birthday;
    }

    private void displayDefaultInfo() {
        tvUserName.setText("运动达人");
        tvUserInfo.setText("完善个人信息");
        if (tvUserEmail != null) tvUserEmail.setText("未设置");
        if (tvUserGender != null) tvUserGender.setText("未设置");
        if (tvUserBirthday != null) tvUserBirthday.setText("未设置");
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

    private void setupClickListeners() {
        LinearLayout favoritePosts = findViewById(R.id.favorite_posts);
        if (favoritePosts != null) {
            favoritePosts.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, FavoritePostsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout favoritePlans = findViewById(R.id.favorite_plans);
        if (favoritePlans != null) {
            favoritePlans.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, FavoritePlansActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout myPlans = findViewById(R.id.my_plans);
        if (myPlans != null) {
            myPlans.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MyPlansActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout myPosts = findViewById(R.id.my_posts);
        if (myPosts != null) {
            myPosts.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, MyPostsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout editProfile = findViewById(R.id.edit_profile);
        if (editProfile != null) {
            editProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ChangeUserInfo.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout logout = findViewById(R.id.logout);
        if (logout != null) {
            logout.setOnClickListener(v -> {
                new SessionManager(ProfileActivity.this).clear();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void setupBottomNavigation() {
        RelativeLayout homeTab = findViewById(R.id.tab_home);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout sportTab = findViewById(R.id.tab_sport);
        if (sportTab != null) {
            sportTab.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, SportPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout forumTab = findViewById(R.id.tab_forum);
        if (forumTab != null) {
            forumTab.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, ForumActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout profileTab = findViewById(R.id.tab_profile);
        if (profileTab != null) {
            profileTab.setOnClickListener(v -> {
            });
        }
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
    protected void onResume() {
        super.onResume();
        loadUserInfo();
        loadPostCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}