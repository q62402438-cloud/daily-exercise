package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CreatePostActivity extends AppCompatActivity {

    private ImageAdapter imageAdapter;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_post);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupRecyclerView();
        setupImagePickers();
        setupClickListeners();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rv_images);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            imageAdapter = new ImageAdapter();
            recyclerView.setAdapter(imageAdapter);

            imageAdapter.setOnImageClickListener(new ImageAdapter.OnImageClickListener() {
                @Override
                public void onAddImageClick() {
                    checkPermissionAndPickImage();
                }

                @Override
                public void onRemoveImageClick(int position) {
                    imageAdapter.removeImage(position);
                }
            });
        }
    }

    private void setupImagePickers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "需要相册权限才能添加图片", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageAdapter.addImage(uri);
                    }
                }
        );
    }

    private void checkPermissionAndPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
    }

    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }

    private void setupClickListeners() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button publishBtn = findViewById(R.id.btn_publish);
        if (publishBtn != null) {
            publishBtn.setOnClickListener(v -> {
                EditText titleInput = findViewById(R.id.et_title);
                EditText contentInput = findViewById(R.id.et_content);

                String title = titleInput != null ? titleInput.getText().toString() : "";
                String content = contentInput != null ? contentInput.getText().toString() : "";

                if (title.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "请输入帖子标题", Toast.LENGTH_SHORT).show();
                } else if (content.isEmpty()) {
                    Toast.makeText(CreatePostActivity.this, "请输入帖子内容", Toast.LENGTH_SHORT).show();
                } else {
                    int imageCount = imageAdapter.getImageList().size();
                    String message = imageCount > 0 ? "发布成功！（包含" + imageCount + "张图片）" : "发布成功！";
                    Toast.makeText(CreatePostActivity.this, message, Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
