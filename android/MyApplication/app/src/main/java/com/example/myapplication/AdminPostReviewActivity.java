package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AdminPostReviewActivity extends AppCompatActivity {

    private RecyclerView rvPosts;
    private PostReviewAdapter postReviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_post_review);

        initViews();
        loadPostList();
        setupClickListeners();
    }

    private void initViews() {
        rvPosts = findViewById(R.id.rv_post_list);
        if (rvPosts != null) {
            rvPosts.setLayoutManager(new LinearLayoutManager(this));
            postReviewAdapter = new PostReviewAdapter();
            rvPosts.setAdapter(postReviewAdapter);

            postReviewAdapter.setOnPostClickListener(post -> {
                Intent intent = new Intent(AdminPostReviewActivity.this, AdminPostReviewDetailActivity.class);
                intent.putExtra("post_id", post.getPostId());
                intent.putExtra("author_name", post.getAuthorName());
                intent.putExtra("post_time", post.getPostTime());
                intent.putExtra("post_title", post.getTitle());
                intent.putExtra("post_content", post.getContent());
                startActivity(intent);
            });
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

        if (postReviewAdapter != null) {
            postReviewAdapter.setPostList(posts);
        }
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
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}