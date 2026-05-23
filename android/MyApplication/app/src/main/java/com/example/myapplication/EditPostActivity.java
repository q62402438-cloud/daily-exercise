package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.PostEntity;
import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPostActivity extends AppCompatActivity {

    private EditText etTitle;
    private EditText etContent;
    private Button btnSave;
    private ImageButton btnBack;
    private ApiService apiService;
    private Integer postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        initViews();
        setupListeners();

        if (getIntent() != null) {
            postId = parsePostId(getIntent().getStringExtra("post_id"));
            if (postId != null) {
                loadPostContent(postId);
            }
        }
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

    private void loadPostContent(Integer postId) {
        PostEntity request = new PostEntity();
        request.setPostID(postId);
        
        apiService.getPostById(request).enqueue(new Callback<Result<PostEntity>>() {
            @Override
            public void onResponse(Call<Result<PostEntity>> call, Response<Result<PostEntity>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    PostEntity post = response.body().getData();
                    if (post.getTitle() != null) {
                        etTitle.setText(post.getTitle());
                    }
                    if (post.getContent() != null) {
                        etContent.setText(post.getContent());
                    }
                } else {
                    Toast.makeText(EditPostActivity.this, "加载帖子内容失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<PostEntity>> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, "加载帖子内容失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        btnSave.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String content = etContent.getText().toString().trim();

            if (title.isEmpty()) {
                Toast.makeText(EditPostActivity.this, "请输入帖子标题", Toast.LENGTH_SHORT).show();
            } else if (content.isEmpty()) {
                Toast.makeText(EditPostActivity.this, "请输入帖子内容", Toast.LENGTH_SHORT).show();
            } else if (postId == null) {
                Toast.makeText(EditPostActivity.this, "帖子ID无效", Toast.LENGTH_SHORT).show();
            } else {
                updatePost(title, content);
            }
        });
    }

    private void updatePost(String title, String content) {
        PostEntity request = new PostEntity();
        request.setPostID(postId);
        request.setTitle(title);
        request.setContent(content);

        apiService.updatePost(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(EditPostActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    String msg = response.body() == null ? "修改失败" : response.body().getMessage();
                    Toast.makeText(EditPostActivity.this, msg == null ? "修改失败" : msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(EditPostActivity.this, "修改失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
