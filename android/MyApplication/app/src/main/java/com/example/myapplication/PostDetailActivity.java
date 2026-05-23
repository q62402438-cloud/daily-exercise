package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.model.CommentEntity;
import com.example.myapplication.model.FavoriteEntity;
import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private boolean isLiked = false;
    private boolean isCollected = false;
    private Integer postId;
    private Integer userId;
    private Integer favoriteId;
    private ApiService apiService;
    private RecyclerView rvComments;
    private CommentAdapter commentAdapter;
    private List<CommentEntity> commentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_post_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        userId = new SessionManager(this).getUserId();
        postId = parsePostId(getIntent() == null ? null : getIntent().getStringExtra("post_id"));
        setupClickListeners();
        setupComments();
        loadPostDetails();
        refreshFavoriteState();
        loadComments();
    }

    private void setupComments() {
        rvComments = findViewById(R.id.rv_comments);
        if (rvComments != null) {
            rvComments.setLayoutManager(new LinearLayoutManager(this));
            commentAdapter = new CommentAdapter(commentList, this::onCommentClick);
            rvComments.setAdapter(commentAdapter);
        }
    }

    private void onCommentClick(CommentEntity comment) {
        Intent intent = new Intent(this, CommentDetailActivity.class);
        intent.putExtra("commentId", comment.getCommentID());
        startActivity(intent);
    }

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout collectLayout = findViewById(R.id.layout_collect);
        if (collectLayout != null) {
            collectLayout.setOnClickListener(v -> toggleCollect());
        }

        LinearLayout commentLayout = findViewById(R.id.layout_comment);
        if (commentLayout != null) {
            commentLayout.setOnClickListener(v -> showCommentDialog());
        }
    }

    private void loadPostDetails() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            String authorName = bundle.getString("author_name", "运动达人小明");
            String postTime = bundle.getString("post_time", "3小时前");
            String title = bundle.getString("post_title", "30天跑步挑战：遇见更好的自己");
            String content = bundle.getString("post_content",
                    "大家好！我是一个跑步爱好者，从去年开始坚持跑步，收获了健康和快乐。现在想发起一个30天跑步挑战，邀请大家一起参与！\n\n【挑战内容】\n每天至少跑步3公里，坚持30天\n\n【参与方式】\n1. 每天打卡记录跑步情况\n2. 可以分享跑步心得和感受\n3. 相互鼓励，共同进步\n\n【活动奖励】\n完成挑战的伙伴们可以获得特别勋章！\n\n有兴趣的朋友可以在评论区留言，我们一起加油！🏃‍♂️");
            String viewCount = bundle.getString("view_count", "328");
            String likeCount = bundle.getString("like_count", "56");
            String commentCount = bundle.getString("comment_count", "23");

            TextView tvAuthorName = findViewById(R.id.tv_author_name);
            TextView tvPostTime = findViewById(R.id.tv_post_time);
            TextView tvPostTitle = findViewById(R.id.tv_post_title);
            TextView tvPostContent = findViewById(R.id.tv_post_content);
            TextView tvViewCount = findViewById(R.id.tv_view_count);

            if (tvAuthorName != null) tvAuthorName.setText(authorName);
            if (tvPostTime != null) tvPostTime.setText(postTime);
            if (tvPostTitle != null) tvPostTitle.setText(title);
            if (tvPostContent != null) tvPostContent.setText(content);
            if (tvViewCount != null) tvViewCount.setText(viewCount + " 人浏览");
            if (postId != null) {
                loadPostById(postId);
            }
        } else {
            setSampleData();
        }
    }

    private void setSampleData() {
        String postId = "";
        if (getIntent() != null && getIntent().getExtras() != null) {
            postId = getIntent().getExtras().getString("post_id", "");
        }

        switch (postId) {
            case "featured_post":
                break;
            default:
                break;
        }
    }

    private void toggleCollect() {
        if (userId == null || postId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCollected) {
            FavoriteEntity request = new FavoriteEntity();
            request.setFavoriteID(favoriteId);
            apiService.deleteFavorite(request).enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                        isCollected = false;
                        favoriteId = null;
                        updateCollectState();
                        Toast.makeText(PostDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PostDetailActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Toast.makeText(PostDetailActivity.this, "取消收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            FavoriteEntity request = new FavoriteEntity();
            request.setUserID(userId);
            request.setTargetID(postId);
            request.setTargetType(2);
            String linkUrl = "pages/post/DisplayPost.html?id=" + postId;
            request.setLinkUrl(linkUrl);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            request.setFavoriteTime(sdf.format(new Date()));
            apiService.addFavorite(request).enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                        Toast.makeText(PostDetailActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
                        refreshFavoriteState();
                    } else {
                        Toast.makeText(PostDetailActivity.this, "收藏失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Toast.makeText(PostDetailActivity.this, "收藏失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showCommentDialog() {
        if (userId == null || postId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }
        EditText input = new EditText(this);
        input.setHint("输入评论内容");
        new AlertDialog.Builder(this)
                .setTitle("发表评论")
                .setView(input)
                .setNegativeButton("取消", null)
                .setPositiveButton("提交", (dialog, which) -> {
                    String content = input.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) {
                        Toast.makeText(this, "评论不能为空", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    submitComment(content);
                })
                .show();
    }

    private void submitComment(String content) {
        CommentEntity request = new CommentEntity();
        request.setPostID(postId);
        request.setUserID(userId);
        request.setContent(content);
        apiService.addComment(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(PostDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                    loadComments();
                } else {
                    Toast.makeText(PostDetailActivity.this, "评论失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "评论失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshFavoriteState() {
        if (userId == null || postId == null) {
            isCollected = false;
            favoriteId = null;
            updateCollectState();
            return;
        }
        FavoriteEntity request = new FavoriteEntity();
        request.setUserID(userId);
        request.setTargetID(postId);
        request.setTargetType(2);
        apiService.checkFavorite(request).enqueue(new Callback<Result<FavoriteEntity>>() {
            @Override
            public void onResponse(Call<Result<FavoriteEntity>> call, Response<Result<FavoriteEntity>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 
                        && response.body().getData() != null && response.body().getData().getFavoriteID() != null) {
                    isCollected = true;
                    favoriteId = response.body().getData().getFavoriteID();
                } else {
                    isCollected = false;
                    favoriteId = null;
                }
                updateCollectState();
            }

            @Override
            public void onFailure(Call<Result<FavoriteEntity>> call, Throwable t) {
                isCollected = false;
                favoriteId = null;
                updateCollectState();
            }
        });
    }

    private void updateCollectState() {
        ImageView collectIcon = findViewById(R.id.iv_collect);
        TextView collectText = findViewById(R.id.tv_collect_text);
        if (collectIcon != null) {
            collectIcon.setImageResource(isCollected ? R.drawable.ic_collect_filled : R.drawable.ic_collect);
        }
        if (collectText != null) {
            collectText.setText(isCollected ? "已收藏" : "收藏");
            collectText.setTextColor(getResources().getColor(isCollected ? android.R.color.holo_orange_dark : android.R.color.darker_gray));
        }
    }

    private void refreshCommentCount() {
        loadComments();
    }

    private void loadComments() {
        if (postId == null) {
            return;
        }
        CommentEntity request = new CommentEntity();
        request.setPostID(postId);
        apiService.getCommentsByPost(request).enqueue(new Callback<Result<List<CommentEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<CommentEntity>>> call, Response<Result<List<CommentEntity>>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200) {
                    List<CommentEntity> comments = response.body().getData();
                    if (comments != null) {
                        commentList.clear();
                        commentList.addAll(comments);
                        commentAdapter.notifyDataSetChanged();
                        
                        TextView tvCommentCount = findViewById(R.id.tv_comment_count);
                        if (tvCommentCount != null) {
                            tvCommentCount.setText(comments.size() + "");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<CommentEntity>>> call, Throwable t) {
                // ignore
            }
        });
    }

    private void loadPostById(Integer id) {
        PostEntity request = new PostEntity();
        request.setPostID(id);
        apiService.getPostById(request).enqueue(new Callback<Result<PostEntity>>() {
            @Override
            public void onResponse(Call<Result<PostEntity>> call, Response<Result<PostEntity>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200 && response.body().getData() != null) {
                    PostEntity post = response.body().getData();
                    TextView tvPostTitle = findViewById(R.id.tv_post_title);
                    TextView tvPostContent = findViewById(R.id.tv_post_content);
                    TextView tvAuthorName = findViewById(R.id.tv_author_name);
                    TextView tvPostTime = findViewById(R.id.tv_post_time);
                    TextView tvViewCount = findViewById(R.id.tv_view_count);
                    if (tvPostTitle != null) tvPostTitle.setText(post.getTitle());
                    if (tvPostContent != null) tvPostContent.setText(post.getContent());
                    if (tvAuthorName != null) tvAuthorName.setText(post.getAuthorName() == null ? "用户" + post.getAuthorID() : post.getAuthorName());
                    if (tvPostTime != null) tvPostTime.setText(post.getPublishTime() == null ? "" : post.getPublishTime());
                    if (tvViewCount != null) tvViewCount.setText((post.getViewCount() == null ? 0 : post.getViewCount()) + " 人浏览");
                }
            }

            @Override
            public void onFailure(Call<Result<PostEntity>> call, Throwable t) {
                // ignore
            }
        });
    }

    private Integer parsePostId(String rawId) {
        if (rawId == null) {
            return null;
        }
        try {
            return Integer.parseInt(rawId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
