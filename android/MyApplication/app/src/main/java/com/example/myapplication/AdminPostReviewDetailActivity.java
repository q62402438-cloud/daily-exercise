package com.example.myapplication;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.Result;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPostReviewDetailActivity extends AppCompatActivity {

    private Integer postId;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_post_review_detail);

        apiService = RetrofitClient.getInstance().create(ApiService.class);
        loadPostDetails();
        setupClickListeners();
    }

    private void loadPostDetails() {
        if (getIntent() != null && getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();

            postId = bundle.getInt("post_id", -1);
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
                if (postId != null && postId != -1) {
                    auditPost(1); // 1表示通过
                } else {
                    Toast.makeText(AdminPostReviewDetailActivity.this, "帖子ID无效", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button rejectBtn = findViewById(R.id.btn_reject);
        if (rejectBtn != null) {
            rejectBtn.setOnClickListener(v -> {
                if (postId != null && postId != -1) {
                    auditPost(2); // 2表示拒绝
                } else {
                    Toast.makeText(AdminPostReviewDetailActivity.this, "帖子ID无效", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void auditPost(int auditState) {
        Map<String, Integer> request = new HashMap<>();
        request.put("postID", postId);
        request.put("auditState", auditState);

        Call<Result<String>> call = apiService.auditPost(request);
        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        String message = auditState == 1 ? "审核通过" : "审核不通过";
                        Toast.makeText(AdminPostReviewDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        String errorMsg = result.getMessage() != null ? result.getMessage() : "操作失败";
                        Toast.makeText(AdminPostReviewDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(AdminPostReviewDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(AdminPostReviewDetailActivity.this, "操作失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}