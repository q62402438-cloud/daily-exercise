package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

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

        highlightCurrentTab("profile");
        setupBottomNavigation();
        setupClickListeners();
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
                
                com.example.myapplication.model.OrdinaryUser user = new com.example.myapplication.model.OrdinaryUser();
                user.setUserID(1);
                user.setUserName("运动达人");
                user.setPhoneNumber("13800138000");
                user.setUserMailbox("user@example.com");
                user.setGender("男");
                user.setBirthday("1995-06-15");
                user.setAge(29);
                user.setWeight(70.5f);
                
                intent.putExtra("user_data", user);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout logout = findViewById(R.id.logout);
        if (logout != null) {
            logout.setOnClickListener(v -> {
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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
