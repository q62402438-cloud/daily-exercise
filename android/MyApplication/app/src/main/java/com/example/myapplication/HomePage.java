package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {

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

        highlightCurrentTab("home");
        initClickListeners();
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

        View featuredPostCard = findViewById(R.id.featured_post_card);
        if (featuredPostCard != null) {
            featuredPostCard.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, PostDetailActivity.class);
                intent.putExtra("post_id", "featured_post");
                intent.putExtra("author_name", "运动达人小明");
                intent.putExtra("post_time", "3小时前");
                intent.putExtra("post_title", "30天跑步挑战：遇见更好的自己");
                intent.putExtra("post_content", "大家好！我是一个跑步爱好者，从去年开始坚持跑步，收获了健康和快乐。现在想发起一个30天跑步挑战，邀请大家一起参与！\n\n【挑战内容】\n每天至少跑步3公里，坚持30天\n\n【参与方式】\n1. 每天打卡记录跑步情况\n2. 可以分享跑步心得和感受\n3. 相互鼓励，共同进步\n\n【活动奖励】\n完成挑战的伙伴们可以获得特别勋章！\n\n有兴趣的朋友可以在评论区留言，我们一起加油！🏃‍♂️");
                intent.putExtra("view_count", "328");
                intent.putExtra("like_count", "56");
                intent.putExtra("comment_count", "23");
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

        View runningPlanCard = findViewById(R.id.running_plan_card);
        if (runningPlanCard != null) {
            runningPlanCard.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, PlanDetailActivity.class);
                intent.putExtra("plan_id", "running_plan");
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        View yogaPlanCard = findViewById(R.id.yoga_plan_card);
        if (yogaPlanCard != null) {
            yogaPlanCard.setOnClickListener(v -> {
                Intent intent = new Intent(HomePage.this, PlanDetailActivity.class);
                intent.putExtra("plan_id", "yoga_plan");
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
}
