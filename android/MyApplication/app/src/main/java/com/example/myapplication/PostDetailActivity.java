package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PostDetailActivity extends AppCompatActivity {

    private boolean isLiked = false;
    private boolean isCollected = false;

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
            String viewCount = bundle.getString("view_count", "328");
            String likeCount = bundle.getString("like_count", "56");
            String commentCount = bundle.getString("comment_count", "23");

            TextView tvAuthorName = findViewById(R.id.tv_author_name);
            TextView tvPostTime = findViewById(R.id.tv_post_time);
            TextView tvPostTitle = findViewById(R.id.tv_post_title);
            TextView tvPostContent = findViewById(R.id.tv_post_content);
            TextView tvViewCount = findViewById(R.id.tv_view_count);
            TextView tvLikeCount = findViewById(R.id.tv_like_count);
            TextView tvCommentCount = findViewById(R.id.tv_comment_count);

            if (tvAuthorName != null) tvAuthorName.setText(authorName);
            if (tvPostTime != null) tvPostTime.setText(postTime);
            if (tvPostTitle != null) tvPostTitle.setText(title);
            if (tvPostContent != null) tvPostContent.setText(content);
            if (tvViewCount != null) tvViewCount.setText(viewCount + " 人浏览");
            if (tvLikeCount != null) tvLikeCount.setText(likeCount + " 点赞");
            if (tvCommentCount != null) tvCommentCount.setText(commentCount + " 评论");
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

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout likeLayout = findViewById(R.id.layout_like);
        if (likeLayout != null) {
            likeLayout.setOnClickListener(v -> toggleLike());
        }

        LinearLayout collectLayout = findViewById(R.id.layout_collect);
        if (collectLayout != null) {
            collectLayout.setOnClickListener(v -> toggleCollect());
        }

        LinearLayout commentLayout = findViewById(R.id.layout_comment);
        if (commentLayout != null) {
            commentLayout.setOnClickListener(v -> {
                Toast.makeText(PostDetailActivity.this, "评论功能开发中...", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void toggleLike() {
        isLiked = !isLiked;
        ImageView likeIcon = findViewById(R.id.iv_like);
        TextView likeText = findViewById(R.id.tv_like_text);
        TextView likeCount = findViewById(R.id.tv_like_count);

        if (isLiked) {
            if (likeIcon != null) {
                likeIcon.setImageResource(R.drawable.ic_like_filled);
            }
            if (likeText != null) {
                likeText.setText("已点赞");
                likeText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }
            if (likeCount != null) {
                String currentCount = likeCount.getText().toString().replace(" 点赞", "");
                int count = Integer.parseInt(currentCount) + 1;
                likeCount.setText(count + " 点赞");
            }
        } else {
            if (likeIcon != null) {
                likeIcon.setImageResource(R.drawable.ic_like);
            }
            if (likeText != null) {
                likeText.setText("点赞");
                likeText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
            if (likeCount != null) {
                String currentCount = likeCount.getText().toString().replace(" 点赞", "");
                int count = Integer.parseInt(currentCount) - 1;
                likeCount.setText(count + " 点赞");
            }
        }
    }

    private void toggleCollect() {
        isCollected = !isCollected;
        ImageView collectIcon = findViewById(R.id.iv_collect);
        TextView collectText = findViewById(R.id.tv_collect_text);

        if (isCollected) {
            if (collectIcon != null) {
                collectIcon.setImageResource(R.drawable.ic_collect_filled);
            }
            if (collectText != null) {
                collectText.setText("已收藏");
                collectText.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
            Toast.makeText(PostDetailActivity.this, "收藏成功！", Toast.LENGTH_SHORT).show();
        } else {
            if (collectIcon != null) {
                collectIcon.setImageResource(R.drawable.ic_collect);
            }
            if (collectText != null) {
                collectText.setText("收藏");
                collectText.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }
            Toast.makeText(PostDetailActivity.this, "已取消收藏", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
