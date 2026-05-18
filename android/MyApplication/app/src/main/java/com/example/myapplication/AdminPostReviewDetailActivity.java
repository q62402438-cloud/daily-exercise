package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AdminPostReviewDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_post_review_detail);

        loadPostDetails();
        setupClickListeners();
    }

    private void loadPostDetails() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            String authorName = bundle.getString("author_name", "运动达人小明");
            String postTime = bundle.getString("post_time", "3小时前");
            String title = bundle.getString("post_title", "30天跑步挑战：遇见更好的自己");
            String content = bundle.getString("post_content",
                    "大家好！我是一个跑步爱好者，从去年开始坚持跑步，收获了健康和快乐。现在想发起一个30天跑步挑战，邀请大家一起参与！\n\n【挑战内容】\n每天至少跑步3公里，坚持30天\n\n【参与方式】\n1. 每天打卡记录跑步情况\n2. 可以分享跑步心得和感受\n3. 相互鼓励，共同进步\n\n【活动奖励】\n完成挑战的伙伴们可以获得特别勋章！\n\n有兴趣的朋友可以在评论区留言，我们一起加油！🏃‍♂️");

            TextView tvAuthorName = findViewById(R.id.tv_author_name);
            TextView tvPostTime = findViewById(R.id.tv_post_time);
            TextView tvPostTitle = findViewById(R.id.tv_post_title);
            TextView tvPostContent = findViewById(R.id.tv_post_content);
            TextView tvStatus = findViewById(R.id.tv_status);

            if (tvAuthorName != null) tvAuthorName.setText(authorName);
            if (tvPostTime != null) tvPostTime.setText(postTime);
            if (tvPostTitle != null) tvPostTitle.setText(title);
            if (tvPostContent != null) tvPostContent.setText(content);
            if (tvStatus != null) {
                tvStatus.setText("待审核");
                tvStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
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

        Button approveBtn = findViewById(R.id.btn_approve);
        if (approveBtn != null) {
            approveBtn.setOnClickListener(v -> {
                Toast.makeText(AdminPostReviewDetailActivity.this,
                        "审核通过",
                        Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button rejectBtn = findViewById(R.id.btn_reject);
        if (rejectBtn != null) {
            rejectBtn.setOnClickListener(v -> {
                Toast.makeText(AdminPostReviewDetailActivity.this,
                        "审核不通过",
                        Toast.LENGTH_SHORT).show();
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