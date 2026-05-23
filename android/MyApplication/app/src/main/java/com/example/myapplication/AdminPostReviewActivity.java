package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPostReviewActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostEntityAdapter postReviewAdapter;
    private ApiService apiService;
    private List<PostEntity> postList = new ArrayList<>();
    private java.util.Map<Integer, String> authorNameMap = new java.util.HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_post_review);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        initViews();
        loadPostList();
        setupClickListeners();
    }

    private void initViews() {
        rvPosts = findViewById(R.id.rv_post_list);
        if (rvPosts != null) {
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postReviewAdapter = new PostEntityAdapter();
            rvPosts.setAdapter(postReviewAdapter);

            postReviewAdapter.setOnPostClickListener(post -> {
                Intent intent = new Intent(AdminPostReviewActivity.this, AdminPostReviewDetailActivity.class);
                Integer postId = post.getPostID();
                if (postId != null) {
                    intent.putExtra("post_id", postId);
                } else {
                    intent.putExtra("post_id", -1);
                }
                intent.putExtra("author_name", post.getAuthorName());
                intent.putExtra("post_time", post.getPublishTime());
                intent.putExtra("post_title", post.getTitle());
                intent.putExtra("post_content", post.getContent());
                startActivity(intent);
            });
        }
    }

    private void loadPostList() {
        Call<Result<List<PostEntity>>> call = apiService.getPendingPosts();
        call.enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<PostEntity>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        postList = result.getData();
                        loadAuthorNames(postList);
                    } else {
                        String message = result.getMessage() != null ? result.getMessage() : "未知错误";
                        Toast.makeText(AdminPostReviewActivity.this, "加载失败: " + message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminPostReviewActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
                Toast.makeText(AdminPostReviewActivity.this, "加载失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAuthorNames(List<PostEntity> posts) {
        authorNameMap.clear();
        int pendingCount = 0;

        for (PostEntity post : posts) {
            Integer authorID = post.getAuthorID();
            String authorName = post.getAuthorName();

            if (authorID != null && (authorName == null || authorName.isEmpty() || "匿名用户".equals(authorName))) {
                if (!authorNameMap.containsKey(authorID)) {
                    pendingCount++;
                    fetchUserName(authorID);
                }
            } else if (authorName != null && !authorName.isEmpty()) {
                authorNameMap.put(authorID, authorName);
            }
        }

        if (pendingCount == 0) {
            updatePostAuthorNames();
        }
    }

    private void fetchUserName(Integer userId) {
        User user = new User();
        user.setUserID(userId);

        apiService.getUserInfo(user).enqueue(new Callback<Result<OrdinaryUser>>() {
            @Override
            public void onResponse(Call<Result<OrdinaryUser>> call, Response<Result<OrdinaryUser>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    String userName = response.body().getData().getUserName();
                    if (userName != null && !userName.isEmpty()) {
                        authorNameMap.put(userId, userName);
                    }
                }
                checkAllFetched();
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                checkAllFetched();
            }
        });
    }

    private void checkAllFetched() {
        boolean allFetched = true;
        for (PostEntity post : postList) {
            Integer authorID = post.getAuthorID();
            String authorName = post.getAuthorName();
            if (authorID != null && (authorName == null || authorName.isEmpty() || "匿名用户".equals(authorName))) {
                if (!authorNameMap.containsKey(authorID)) {
                    allFetched = false;
                    break;
                }
            }
        }

        if (allFetched) {
            updatePostAuthorNames();
        }
    }

    private void updatePostAuthorNames() {
        for (PostEntity post : postList) {
            Integer authorID = post.getAuthorID();
            String authorName = post.getAuthorName();
            if (authorID != null && (authorName == null || authorName.isEmpty() || "匿名用户".equals(authorName))) {
                String realName = authorNameMap.get(authorID);
                if (realName != null && !realName.isEmpty()) {
                    post.setAuthorName(realName);
                }
            }
        }
        postReviewAdapter.setPostList(postList);
    }

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPostList(); // 刷新数据
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}