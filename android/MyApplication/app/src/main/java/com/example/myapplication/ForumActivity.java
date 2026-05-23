package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
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

import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForumActivity extends AppCompatActivity {
    private static final String TAG = "ForumActivity";

    private EditText etSearch;
    private ViewPager2 vpFeaturedPosts;
    private RecyclerView rvPostList;
    private SwipeRefreshLayout swipeRefresh;
    private FeaturedPostAdapter featuredPostAdapter;
    private PostAdapter postAdapter;
    private ApiService apiService;

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
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        loadPostList();
        setupBottomNavigation();
        setupClickListeners();
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
                loadPostList();
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

    private void loadFeaturedPosts(List<Post> allPosts) {
        if (vpFeaturedPosts != null) {
            List<Post> featuredPosts = new ArrayList<>();
            int take = Math.min(3, allPosts.size());
            for (int i = 0; i < take; i++) {
                featuredPosts.add(allPosts.get(i));
            }

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
        Map<String, Integer> request = new HashMap<>();
        request.put("page", 1);
        request.put("pageSize", 20);
        apiService.getPosts(request).enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    List<Post> posts = mapPosts(response.body().getData());
                    if (postAdapter != null) {
                        postAdapter.setPostList(posts);
                    }
                    loadFeaturedPosts(posts);
                    if (posts.isEmpty()) {
                        Toast.makeText(ForumActivity.this, "暂无帖子", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ForumActivity.this, "加载帖子失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
                Log.e(TAG, "load posts failed", t);
                Toast.makeText(ForumActivity.this, "网络异常，加载帖子失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Post> mapPosts(List<PostEntity> entities) {
        List<Post> posts = new ArrayList<>();
        for (PostEntity entity : entities) {
            Integer postId = entity.getPostID();
            Integer authorId = entity.getAuthorID();
            String authorName = entity.getAuthorName();
            if (authorName == null || authorName.isEmpty()) {
                authorName = authorId == null ? "用户" : "用户" + authorId;
            }
            int auditState = entity.getAuditState() != null ? entity.getAuditState() : 1;
            posts.add(new Post(
                    postId == null ? "" : String.valueOf(postId),
                    authorName,
                    safe(entity.getTitle()),
                    safe(entity.getContent()),
                    safe(entity.getPublishTime()),
                    String.valueOf(entity.getViewCount() == null ? 0 : entity.getViewCount()),
                    String.valueOf(entity.getLikeCount() == null ? 0 : entity.getLikeCount()),
                    String.valueOf(entity.getCommentCount() == null ? 0 : entity.getCommentCount()),
                    0,
                    auditState
            ));
        }
        return posts;
    }

    private String safe(String value) {
        return value == null ? "" : value;
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
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }

        ImageButton searchBtn = findViewById(R.id.btn_search);
        if (searchBtn != null) {
            searchBtn.setOnClickListener(v -> {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) {
                    Intent intent = new Intent(ForumActivity.this, SearchResultActivity.class);
                    intent.putExtra("keyword", keyword);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                } else {
                    Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
                }
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
