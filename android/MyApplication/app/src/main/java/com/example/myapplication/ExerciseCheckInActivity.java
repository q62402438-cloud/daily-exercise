package com.example.myapplication;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ExerciseCheckInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_check_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button finishBtn = findViewById(R.id.btn_finish);
        if (finishBtn != null) {
            finishBtn.setOnClickListener(v -> {
                EditText startTime = findViewById(R.id.et_start_time);
                EditText endTime = findViewById(R.id.et_end_time);
                EditText duration = findViewById(R.id.et_duration);
                EditText exerciseAmount = findViewById(R.id.et_exercise_amount);
                EditText calorie = findViewById(R.id.et_calorie);
                Spinner sportType = findViewById(R.id.sp_sport_type);

                String start = startTime != null ? startTime.getText().toString() : "";
                String end = endTime != null ? endTime.getText().toString() : "";
                String dur = duration != null ? duration.getText().toString() : "";

                if (start.isEmpty() || end.isEmpty() || dur.isEmpty()) {
                    Toast.makeText(ExerciseCheckInActivity.this, "请填写完整的运动信息", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ExerciseCheckInActivity.this, "打卡成功！", Toast.LENGTH_SHORT).show();
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
