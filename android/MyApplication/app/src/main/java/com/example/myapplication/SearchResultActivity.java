package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SearchResultActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnBack;
    private ImageButton btnClear;
    private Button btnSearch;
    private RecyclerView rvSearchResult;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;
    private TextView tvSearchResult;

    private PostAdapter postAdapter;
    private ApiService apiService;
    private String currentKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        initViews();
        setupListeners();

        String keyword = getIntent().getStringExtra("keyword");
        if (keyword != null && !keyword.isEmpty()) {
            currentKeyword = keyword;
            etSearch.setText(keyword);
            performSearch(keyword);
        }
    }

    private void initViews() {
        etSearch = findViewById(R.id.et_search);
        btnBack = findViewById(R.id.btn_back);
        btnClear = findViewById(R.id.btn_clear);
        btnSearch = findViewById(R.id.btn_search);
        rvSearchResult = findViewById(R.id.rv_search_result);
        layoutEmpty = findViewById(R.id.layout_empty);
        progressBar = findViewById(R.id.progress_bar);
        tvSearchResult = findViewById(R.id.tv_search_result);

        rvSearchResult.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter();
        rvSearchResult.setAdapter(postAdapter);

        postAdapter.setOnPostClickListener(post -> {
            Intent intent = new Intent(SearchResultActivity.this, PostDetailActivity.class);
            intent.putExtra("post_id", post.getPostId());
            intent.putExtra("author_name", post.getAuthorName());
            intent.putExtra("post_time", post.getPostTime());
            intent.putExtra("post_title", post.getTitle());
            intent.putExtra("post_content", post.getContent());
            intent.putExtra("view_count", post.getViewCount());
            intent.putExtra("like_count", post.getLikeCount());
            intent.putExtra("comment_count", post.getCommentCount());
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            postAdapter.setPostList(new ArrayList<>());
            layoutEmpty.setVisibility(View.GONE);
            tvSearchResult.setText("");
        });

        btnSearch.setOnClickListener(v -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                currentKeyword = keyword;
                performSearch(keyword);
            } else {
                Toast.makeText(SearchResultActivity.this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            }
        });

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            if (!keyword.isEmpty()) {
                currentKeyword = keyword;
                performSearch(keyword);
            } else {
                Toast.makeText(SearchResultActivity.this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    private void performSearch(String keyword) {
        showLoading(true);
        layoutEmpty.setVisibility(View.GONE);

        Map<String, Integer> request = new HashMap<>();
        request.put("page", 1);
        request.put("pageSize", 100);

        apiService.getPosts(request).enqueue(new Callback<Result<List<PostEntity>>>() {
            @Override
            public void onResponse(Call<Result<List<PostEntity>>> call, Response<Result<List<PostEntity>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200) {
                    List<PostEntity> entities = response.body().getData();
                    if (entities != null && !entities.isEmpty()) {
                        List<PostEntity> filteredEntities = filterPostsByKeyword(entities, keyword);
                        if (!filteredEntities.isEmpty()) {
                            List<Post> posts = mapPosts(filteredEntities);
                            postAdapter.setPostList(posts);
                            tvSearchResult.setText("搜索结果：共找到 " + posts.size() + " 条");
                            rvSearchResult.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                        } else {
                            rvSearchResult.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                            tvSearchResult.setText("搜索结果：共找到 0 条");
                        }
                    } else {
                        rvSearchResult.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                        tvSearchResult.setText("搜索结果：共找到 0 条");
                    }
                } else {
                    rvSearchResult.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    tvSearchResult.setText("搜索结果：共找到 0 条");
                }
            }

            @Override
            public void onFailure(Call<Result<List<PostEntity>>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SearchResultActivity.this, "搜索失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                rvSearchResult.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private List<PostEntity> filterPostsByKeyword(List<PostEntity> entities, String keyword) {
        List<PostEntity> filtered = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        
        for (PostEntity entity : entities) {
            String title = entity.getTitle();
            String content = entity.getContent();
            String authorName = entity.getAuthorName();
            
            boolean titleMatch = title != null && title.toLowerCase().contains(lowerKeyword);
            boolean contentMatch = content != null && content.toLowerCase().contains(lowerKeyword);
            boolean authorMatch = authorName != null && authorName.toLowerCase().contains(lowerKeyword);
            
            if (titleMatch || contentMatch || authorMatch) {
                filtered.add(entity);
            }
        }
        
        return filtered;
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
        return posts;
    }

    private void showLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        rvSearchResult.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
