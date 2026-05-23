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

public class FavoritePostsActivity extends AppCompatActivity {
    private FavoritePostAdapter favoritePostAdapter;
    private List<Post> postList = new ArrayList<>();
    private List<FavoriteEntity> favoriteEntities = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getInstance().create(ApiService.class);

        setupRecyclerView();
        setupClickListeners();
        loadFavoritePosts();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_favorite_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        favoritePostAdapter = new FavoritePostAdapter(postList, new FavoritePostAdapter.OnFavoriteActionListener() {
            @Override
            public void onPostClick(Post post) {
                Intent intent = new Intent(FavoritePostsActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", post.getPostId());
                intent.putExtra("author_name", post.getAuthorName());
                intent.putExtra("post_time", post.getPostTime());
                intent.putExtra("post_title", post.getTitle());
                intent.putExtra("post_content", post.getContent());
                intent.putExtra("view_count", post.getViewCount());
                intent.putExtra("like_count", post.getLikeCount());
                intent.putExtra("comment_count", post.getCommentCount());
                startActivity(intent);
            }

            @Override
            public void onUnfavoriteClick(Post post, int position) {
                showUnfavoriteConfirmDialog(post, position);
            }
        });
        recyclerView.setAdapter(favoritePostAdapter);
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

    private void loadFavoritePosts() {
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
                    List<FavoriteEntity> favoritePosts = new ArrayList<>();
                    for (FavoriteEntity favoriteEntity : response.body().getData()) {
                        if (favoriteEntity.getTargetType() != null && favoriteEntity.getTargetType() == 2) {
                            favoriteEntities.add(favoriteEntity);
                            favoritePosts.add(favoriteEntity);
                        }
                    }
                    loadPostDetails(favoritePosts);
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

    private void loadPostDetails(List<FavoriteEntity> favoritePosts) {
        if (favoritePosts.isEmpty()) {
            findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
            postList.clear();
            favoritePostAdapter.notifyDataSetChanged();
            return;
        }
        postList.clear();
        final int[] remain = {favoritePosts.size()};
        for (FavoriteEntity favorite : favoritePosts) {
            PostEntity req = new PostEntity();
            req.setPostID(favorite.getTargetID());
            apiService.getPostById(req).enqueue(new Callback<Result<PostEntity>>() {
                @Override
                public void onResponse(Call<Result<PostEntity>> call, Response<Result<PostEntity>> response) {
                    remain[0]--;
                    if (response.isSuccessful() && response.body() != null
                            && response.body().getCode() == 200 && response.body().getData() != null) {
                        PostEntity entity = response.body().getData();
                        postList.add(new Post(
                                String.valueOf(entity.getPostID()),
                                entity.getAuthorName() == null ? "用户" : entity.getAuthorName(),
                                entity.getTitle() == null ? "" : entity.getTitle(),
                                entity.getContent() == null ? "" : entity.getContent(),
                                entity.getPublishTime() == null ? "" : entity.getPublishTime(),
                                String.valueOf(entity.getViewCount() == null ? 0 : entity.getViewCount()),
                                String.valueOf(entity.getLikeCount() == null ? 0 : entity.getLikeCount()),
                                String.valueOf(entity.getCommentCount() == null ? 0 : entity.getCommentCount())
                        ));
                    }
                    maybeDone(remain[0]);
                }

                @Override
                public void onFailure(Call<Result<PostEntity>> call, Throwable t) {
                    remain[0]--;
                    maybeDone(remain[0]);
                }
            });
        }
    }

    private void maybeDone(int remain) {
        if (remain == 0) {
            findViewById(R.id.tv_empty).setVisibility(postList.isEmpty() ? View.VISIBLE : View.GONE);
            favoritePostAdapter.notifyDataSetChanged();
        }
    }

    private void showUnfavoriteConfirmDialog(Post post, int position) {
        new AlertDialog.Builder(this)
                .setTitle("取消收藏")
                .setMessage("确定要取消收藏这篇帖子吗？")
                .setPositiveButton("确认", (dialog, which) -> {
                    unfavoritePost(post, position);
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void unfavoritePost(Post post, int position) {
        if (position >= 0 && position < favoriteEntities.size()) {
            FavoriteEntity favorite = favoriteEntities.get(position);
            if (favorite.getFavoriteID() != null) {
                apiService.deleteFavorite(favorite).enqueue(new Callback<Result<String>>() {
                    @Override
                    public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                        if (response.isSuccessful() && response.body() != null
                                && response.body().getCode() == 200) {
                            Toast.makeText(FavoritePostsActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                            favoriteEntities.remove(position);
                            favoritePostAdapter.removeItem(position);
                            
                            if (postList.isEmpty()) {
                                findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(FavoritePostsActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Result<String>> call, Throwable t) {
                        Toast.makeText(FavoritePostsActivity.this, "取消收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    private void showClearAllConfirmDialog() {
        if (postList.isEmpty()) {
            Toast.makeText(this, "暂无收藏可清空", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("清空所有收藏")
                .setMessage("确定要清空所有收藏的帖子吗？此操作不可恢复！")
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
                                Toast.makeText(FavoritePostsActivity.this, "已清空所有收藏", Toast.LENGTH_SHORT).show();
                                postList.clear();
                                favoriteEntities.clear();
                                favoritePostAdapter.notifyDataSetChanged();
                                findViewById(R.id.tv_empty).setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(FavoritePostsActivity.this, "部分收藏清空失败", Toast.LENGTH_SHORT).show();
                                loadFavoritePosts();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Result<String>> call, Throwable t) {
                        remain[0]--;
                        allSuccess[0] = false;
                        if (remain[0] == 0) {
                            Toast.makeText(FavoritePostsActivity.this, "清空收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
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
