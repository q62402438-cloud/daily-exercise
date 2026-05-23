package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.CommentEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDetailActivity extends AppCompatActivity {

    private TextView tvAuthor, tvContent, tvTime, tvLikeCount;
    private Button btnReply, btnLike, btnEdit, btnDelete, btnBack;
    private ApiService apiService;
    private SessionManager sessionManager;
    private Integer commentId;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);

        initViews();
        initData();
        loadCommentDetail();
    }

    private void initViews() {
        tvAuthor = findViewById(R.id.tv_comment_author);
        tvContent = findViewById(R.id.tv_comment_content);
        tvTime = findViewById(R.id.tv_comment_time);
        tvLikeCount = findViewById(R.id.tv_like_count);
        btnBack = findViewById(R.id.btn_back_to_post);
        btnReply = findViewById(R.id.btn_reply);
        btnLike = findViewById(R.id.btn_like);
        btnEdit = findViewById(R.id.btn_edit);
        btnDelete = findViewById(R.id.btn_delete);

        btnBack.setOnClickListener(v -> finish());
        btnReply.setOnClickListener(v -> replyComment());
        btnLike.setOnClickListener(v -> likeComment());
        btnEdit.setOnClickListener(v -> editComment());
        btnDelete.setOnClickListener(v -> deleteComment());
    }

    private void initData() {
        apiService = RetrofitClient.getInstance().create(ApiService.class);
        sessionManager = new SessionManager(this);
        userId = sessionManager.getUserId();
        commentId = getIntent().getIntExtra("commentId", -1);
    }

    private void loadCommentDetail() {
        if (commentId == -1) {
            Toast.makeText(this, "评论ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        CommentEntity request = new CommentEntity();
        request.setCommentID(commentId);

        apiService.getCommentById(request).enqueue(new Callback<Result<CommentEntity>>() {
            @Override
            public void onResponse(Call<Result<CommentEntity>> call, Response<Result<CommentEntity>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200) {
                    CommentEntity comment = response.body().getData();
                    if (comment != null) {
                        tvAuthor.setText(comment.getUserName());
                        tvContent.setText(comment.getContent());
                        tvTime.setText(comment.getPublishTime());
                        tvLikeCount.setText("点赞数: 0");
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<CommentEntity>> call, Throwable t) {
                Toast.makeText(CommentDetailActivity.this, "加载评论失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void replyComment() {
        Toast.makeText(this, "回复评论功能", Toast.LENGTH_SHORT).show();
    }

    private void likeComment() {
        int currentLikes = Integer.parseInt(tvLikeCount.getText().toString().replace("点赞数: ", ""));
        currentLikes++;
        tvLikeCount.setText("点赞数: " + currentLikes);
        Toast.makeText(this, "点赞成功", Toast.LENGTH_SHORT).show();
    }

    private void editComment() {
        finish();
    }

    private void deleteComment() {
        if (commentId == -1) {
            Toast.makeText(this, "评论ID无效", Toast.LENGTH_SHORT).show();
            return;
        }

        CommentEntity request = new CommentEntity();
        request.setCommentID(commentId);

        apiService.deleteComment(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200) {
                    Toast.makeText(CommentDetailActivity.this, "评论已删除", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CommentDetailActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(CommentDetailActivity.this, "删除失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
