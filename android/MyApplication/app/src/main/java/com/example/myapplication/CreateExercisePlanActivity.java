package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class CreateExercisePlanActivity extends AppCompatActivity {

    private TextView tvStartDate;
    private TextView tvEndDate;
    private SwitchMaterial switchPublic;
    private String startDateStr = "";
    private String endDateStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_exercise_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvStartDate = findViewById(R.id.tv_start_date);
        tvEndDate = findViewById(R.id.tv_end_date);
        switchPublic = findViewById(R.id.switch_public);

        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        LinearLayout layoutStartDate = findViewById(R.id.layout_start_date);
        if (layoutStartDate != null) {
            layoutStartDate.setOnClickListener(v -> showDatePicker(true));
        }

        LinearLayout layoutEndDate = findViewById(R.id.layout_end_date);
        if (layoutEndDate != null) {
            layoutEndDate.setOnClickListener(v -> showDatePicker(false));
        }

        Button saveBtn = findViewById(R.id.btn_save);
        if (saveBtn != null) {
            saveBtn.setOnClickListener(v -> {
                EditText planName = findViewById(R.id.et_plan_name);
                EditText dailyExercise = findViewById(R.id.et_daily_exercise);
                EditText dailyCalorie = findViewById(R.id.et_daily_calorie);
                Spinner sportType = findViewById(R.id.sp_sport_type);

                String name = planName != null ? planName.getText().toString() : "";
                String dailyEx = dailyExercise != null ? dailyExercise.getText().toString() : "";
                String dailyCal = dailyCalorie != null ? dailyCalorie.getText().toString() : "";

                if (name.isEmpty()) {
                    Toast.makeText(CreateExercisePlanActivity.this, "请填写计划名称", Toast.LENGTH_SHORT).show();
                } else if (startDateStr.isEmpty()) {
                    Toast.makeText(CreateExercisePlanActivity.this, "请选择开始日期", Toast.LENGTH_SHORT).show();
                } else if (endDateStr.isEmpty()) {
                    Toast.makeText(CreateExercisePlanActivity.this, "请选择结束日期", Toast.LENGTH_SHORT).show();
                } else {
                    boolean isPublic = switchPublic != null && switchPublic.isChecked();
                    String visibilityText = isPublic ? "公开" : "私密";
                    Toast.makeText(CreateExercisePlanActivity.this,
                            "计划创建成功！（" + visibilityText + "）",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                }
            });
        }
    }

    private void showDatePicker(boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, month1, dayOfMonth) -> {
                    String dateStr = String.format("%d-%02d-%02d", year1, month1 + 1, dayOfMonth);
                    if (isStartDate) {
                        startDateStr = dateStr;
                        tvStartDate.setText(dateStr);
                        tvStartDate.setHint("");
                    } else {
                        endDateStr = dateStr;
                        tvEndDate.setText(dateStr);
                        tvEndDate.setHint("");
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}
