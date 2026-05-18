package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private EditText etSearch;
    private ViewPager2 vpFeaturedPosts;
    private RecyclerView rvPostList;
    private SwipeRefreshLayout swipeRefresh;
    private FeaturedPostAdapter featuredPostAdapter;
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forum);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        highlightCurrentTab("forum");
        loadFeaturedPosts();
        loadPostList();
        setupBottomNavigation();
        setupClickListeners();
    }

    private void highlightCurrentTab(String currentTab) {
        ImageView homeIcon = findViewById(R.id.icon_home);
        ImageView sportIcon = findViewById(R.id.icon_sport);
        ImageView forumIcon = findViewById(R.id.icon_forum);
        ImageView profileIcon = findViewById(R.id.icon_profile);

        if (homeIcon != null) {
            homeIcon.setImageResource(currentTab.equals("home") ? R.drawable.ic_home_active : R.drawable.ic_home);
        }
        if (sportIcon != null) {
            sportIcon.setImageResource(currentTab.equals("sport") ? R.drawable.ic_sports_active : R.drawable.ic_sports);
        }
        if (forumIcon != null) {
            forumIcon.setImageResource(currentTab.equals("forum") ? R.drawable.ic_forum_active : R.drawable.ic_forum);
        }
        if (profileIcon != null) {
            profileIcon.setImageResource(currentTab.equals("profile") ? R.drawable.ic_profile_active : R.drawable.ic_profile);
        }
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        vpFeaturedPosts = findViewById(R.id.vp_featured_posts);
        rvPostList = findViewById(R.id.rv_post_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);

        if (swipeRefresh != null) {
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_green_light,
                    android.R.color.holo_green_dark
            );
            swipeRefresh.setOnRefreshListener(() -> {
                new Handler().postDelayed(() -> {
                    loadPostList();
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(this, "刷新成功", Toast.LENGTH_SHORT).show();
                }, 1000);
            });
        }

        if (rvPostList != null) {
            rvPostList.setLayoutManager(new LinearLayoutManager(this));
            postAdapter = new PostAdapter();
            rvPostList.setAdapter(postAdapter);

            postAdapter.setOnPostClickListener(post -> {
                Intent intent = new Intent(ForumActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", post.getPostId());
                intent.putExtra("author_name", post.getAuthorName());
                intent.putExtra("post_time", post.getPostTime());
                intent.putExtra("post_title", post.getTitle());
                intent.putExtra("post_content", post.getContent());
                intent.putExtra("view_count", post.getViewCount());
                intent.putExtra("like_count", post.getLikeCount());
                intent.putExtra("comment_count", post.getCommentCount());
                startActivity(intent);
            });
        }
    }

    private void loadFeaturedPosts() {
        if (vpFeaturedPosts != null) {
            List<Post> featuredPosts = new ArrayList<>();

            featuredPosts.add(new Post("featured_1", "运动达人小明",
                    "30天跑步挑战：遇见更好的自己",
                    "大家好！我是一个跑步爱好者，从去年开始坚持跑步，收获了健康和快乐。",
                    "5小时前", "328", "56", "23"));

            featuredPosts.add(new Post("featured_2", "瑜伽爱好者",
                    "清晨瑜伽放松计划",
                    "每天清晨花30分钟做瑜伽，感觉整个人都轻松了很多。",
                    "昨天", "256", "89", "45"));

            featuredPosts.add(new Post("featured_3", "健身教练小李",
                    "居家健身计划分享",
                    "在家也能锻炼！分享一套简单有效的健身动作，适合上班族。",
                    "2天前", "512", "134", "67"));

            featuredPostAdapter = new FeaturedPostAdapter(featuredPosts, post -> {
                Intent intent = new Intent(ForumActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", post.getPostId());
                intent.putExtra("author_name", post.getAuthorName());
                intent.putExtra("post_time", post.getPostTime());
                intent.putExtra("post_title", post.getTitle());
                intent.putExtra("post_content", post.getContent());
                intent.putExtra("view_count", post.getViewCount());
                intent.putExtra("like_count", post.getLikeCount());
                intent.putExtra("comment_count", post.getCommentCount());
                startActivity(intent);
            });
            vpFeaturedPosts.setAdapter(featuredPostAdapter);
        }
    }

    private void loadPostList() {
        List<Post> posts = new ArrayList<>();

        posts.add(new Post("post_1", "健康生活家",
                "健康饮食搭配建议",
                "运动配合健康饮食效果更好！给大家分享一些简单的健康食谱...",
                "3天前", "189", "45", "23"));

        posts.add(new Post("post_2", "晨跑爱好者",
                "早起晨跑的心得体会",
                "坚持晨跑三个月了，分享一些心得和遇到的问题解决方案...",
                "1周前", "423", "78", "34"));

        posts.add(new Post("post_3", "力量训练达人",
                "无器械健身动作分享",
                "不需要去健身房，也能练出好身材！分享几个在家就能做的力量训练动作...",
                "1周前", "567", "156", "89"));

        posts.add(new Post("post_4", "跑步新手",
                "我的第一个半马训练记录",
                "从零开始到完成半马，分享我的训练计划和心得...",
                "2周前", "734", "234", "123"));

        posts.add(new Post("post_5", "营养师小美",
                "运动后的营养补充",
                "运动后吃什么？给大家分享一些科学的营养补充建议...",
                "2周前", "298", "67", "45"));

        posts.add(new Post("post_6", "游泳爱好者",
                "游泳入门指南",
                "夏天到了，学游泳正当时！分享一些游泳入门技巧...",
                "3周前", "412", "98", "56"));

        if (postAdapter != null) {
            postAdapter.setPostList(posts);
        }
    }

    private void setupClickListeners() {
        ImageButton newPostBtn = findViewById(R.id.btn_new_post);
        if (newPostBtn != null) {
            newPostBtn.setOnClickListener(v -> {
                Intent intent = new Intent(ForumActivity.this, CreatePostActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout searchLayout = findViewById(R.id.layout_search);
        if (searchLayout != null) {
            searchLayout.setOnClickListener(v -> {
                if (etSearch != null) {
                    etSearch.setFocusable(true);
                    etSearch.setFocusableInTouchMode(true);
                    etSearch.requestFocus();
                }
            });
        }

        if (etSearch != null) {
            etSearch.setOnEditorActionListener((v, actionId, event) -> {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    Intent intent = new Intent(ForumActivity.this, SearchResultActivity.class);
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }
    }

    private void setupBottomNavigation() {
        RelativeLayout homeTab = findViewById(R.id.tab_home);
        if (homeTab != null) {
            homeTab.setOnClickListener(v -> {
                Intent intent = new Intent(ForumActivity.this, HomePage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            });
        }

        RelativeLayout sportTab = findViewById(R.id.tab_sport);
        if (sportTab != null) {
            sportTab.setOnClickListener(v -> {
                Intent intent = new Intent(ForumActivity.this, SportPage.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
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
                Intent intent = new Intent(ForumActivity.this, ProfileActivity.class);
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
