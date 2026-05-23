package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostsActivity extends AppCompatActivity {
    private PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView recyclerView = findViewById(R.id.rv_my_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        recyclerView.setAdapter(postAdapter);
        postAdapter.setOnPostClickListener(post -> {
            Intent intent = new Intent(MyPostsActivity.this, PostDetailActivity.class);
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

        RelativeLayout backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
        loadMyPosts();
    }

    private void loadMyPosts() {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            return;
        }
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getPostsByAuthor(userId).enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                List<Post> posts = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    for (PostEntity entity : response.body().getData()) {
                        int auditState = entity.getAuditState() != null ? entity.getAuditState() : 1;
                        posts.add(new Post(
                                String.valueOf(entity.getPostID()),
                                entity.getAuthorName() == null ? "用户" + userId : entity.getAuthorName(),
                                entity.getTitle() == null ? "" : entity.getTitle(),
                                entity.getContent() == null ? "" : entity.getContent(),
                                entity.getPublishTime() == null ? "" : entity.getPublishTime(),
                                String.valueOf(entity.getViewCount() == null ? 0 : entity.getViewCount()),
                                String.valueOf(entity.getLikeCount() == null ? 0 : entity.getLikeCount()),
                                String.valueOf(entity.getCommentCount() == null ? 0 : entity.getCommentCount()),
                                0,
                                auditState
                        ));
                    }
                }
                findViewById(R.id.tv_empty).setVisibility(posts.isEmpty() ? android.view.View.VISIBLE : android.view.View.GONE);
                postAdapter.setPostList(posts);
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
                findViewById(R.id.tv_empty).setVisibility(android.view.View.VISIBLE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
