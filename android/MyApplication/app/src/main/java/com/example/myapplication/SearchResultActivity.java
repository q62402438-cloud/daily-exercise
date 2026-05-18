package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private PostAdapter postAdapter;
    private String searchKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_result);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchKeyword = getIntent().getStringExtra("keyword");
        if (searchKeyword == null) {
            searchKeyword = "";
        }

        highlightCurrentTab("forum");
        initViews();
        loadSearchResults();
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

    private void initViews() {
        TextView tvTitle = findViewById(R.id.tv_title);
        if (tvTitle != null) {
            tvTitle.setText("搜索: " + searchKeyword);
        }

        recyclerView = findViewById(R.id.rv_search_results);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            postAdapter = new PostAdapter();
            recyclerView.setAdapter(postAdapter);
        }

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_green_light,
                    android.R.color.holo_green_dark
            );
            swipeRefresh.setOnRefreshListener(() -> {
                new Handler().postDelayed(() -> {
                    loadSearchResults();
                    swipeRefresh.setRefreshing(false);
                }, 1000);
            });
        }
    }

    private void loadSearchResults() {
        List<Post> searchResults = new ArrayList<>();

        if (!searchKeyword.isEmpty()) {
            List<Post> allPosts = getSamplePosts();
            for (Post post : allPosts) {
                if (post.getTitle().contains(searchKeyword) ||
                        post.getContent().contains(searchKeyword)) {
                    searchResults.add(post);
                }
            }
        }

        if (searchResults.isEmpty()) {
            View emptyView = findViewById(R.id.layout_empty);
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
            }
            Toast.makeText(this, "未找到相关帖子", Toast.LENGTH_SHORT).show();
        } else {
            View emptyView = findViewById(R.id.layout_empty);
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
            }
        }

        if (postAdapter != null) {
            postAdapter.setPostList(searchResults);
        }
    }

    private List<Post> getSamplePosts() {
        List<Post> posts = new ArrayList<>();

        posts.add(new Post("post_1", "运动达人小明",
                "30天跑步挑战：遇见更好的自己",
                "大家好！我是一个跑步爱好者，从去年开始坚持跑步，收获了健康和快乐。现在想发起一个30天跑步挑战...",
                "5小时前", "328", "56", "23"));

        posts.add(new Post("post_2", "瑜伽爱好者",
                "清晨瑜伽放松计划",
                "每天清晨花30分钟做瑜伽，感觉整个人都轻松了很多。分享我的瑜伽计划给大家...",
                "昨天", "256", "89", "45"));

        posts.add(new Post("post_3", "健身教练小李",
                "居家健身计划分享",
                "在家也能锻炼！分享一套简单有效的健身动作，不需要器械，适合上班族...",
                "2天前", "512", "134", "67"));

        posts.add(new Post("post_4", "健康生活家",
                "健康饮食搭配建议",
                "运动配合健康饮食效果更好！给大家分享一些简单的健康食谱...",
                "3天前", "189", "45", "23"));

        posts.add(new Post("post_5", "晨跑爱好者",
                "早起晨跑的心得体会",
                "坚持晨跑三个月了，分享一些心得和遇到的问题解决方案...",
                "1周前", "423", "78", "34"));

        return posts;
    }

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout homeTab = findViewById(R.id.tab_home);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                Intent intent = new Intent(SearchResultActivity.this, HomePage.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout sportTab = findViewById(R.id.tab_sport);
        if (sportTab != null) {
            sportTab.setOnClickListener(v -> {
                Intent intent = new Intent(SearchResultActivity.this, SportPage.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout forumTab = findViewById(R.id.tab_forum);
        if (forumTab != null) {
            forumTab.setOnClickListener(v -> {
            });
        }

        RelativeLayout profileTab = findViewById(R.id.tab_profile);
        if (profileTab != null) {
            profileTab.setOnClickListener(v -> {
                Intent intent = new Intent(SearchResultActivity.this, ProfileActivity.class);
                startActivity(intent);
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
