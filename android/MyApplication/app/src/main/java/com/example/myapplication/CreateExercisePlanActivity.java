package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.switchmaterial.SwitchMaterial;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Calendar;

public class CreateExercisePlanActivity extends AppCompatActivity {

    private TextView tvStartDate;
    private TextView tvEndDate;
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

        setupSpinner();

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
                RadioGroup rgExerciseType = findViewById(R.id.rg_exercise_type);
                Spinner spExerciseAmount = findViewById(R.id.sp_exercise_amount);
                EditText etDetail = findViewById(R.id.et_detail);

                String name = planName != null ? planName.getText().toString() : "";
                String detail = etDetail != null ? etDetail.getText().toString() : "";

                if (name.isEmpty()) {
                    Toast.makeText(CreateExercisePlanActivity.this, "请填写计划名称", Toast.LENGTH_SHORT).show();
                } else if (startDateStr.isEmpty()) {
                    Toast.makeText(CreateExercisePlanActivity.this, "请选择开始日期", Toast.LENGTH_SHORT).show();
                } else if (endDateStr.isEmpty()) {
                    Toast.makeText(CreateExercisePlanActivity.this, "请选择结束日期", Toast.LENGTH_SHORT).show();
                } else {
                    String sportName = getSelectedSportType();
                    String exerciseAmount = getSelectedExerciseAmount(spExerciseAmount);
                    createPlan(name, sportName, detail, exerciseAmount);
                }
            });
        }
    }

    private void setupSpinner() {
        Spinner spinner = findViewById(R.id.sp_exercise_amount);
        if (spinner != null) {
            String[] amounts = {"15分钟", "30分钟", "45分钟", "60分钟", "90分钟", "120分钟"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_item, amounts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private String getSelectedSportType() {
        RadioGroup rgExerciseType = findViewById(R.id.rg_exercise_type);
        if (rgExerciseType == null) return "其他运动";

        int checkedId = rgExerciseType.getCheckedRadioButtonId();
        if (checkedId == R.id.rb_fat_loss) {
            return "减脂运动";
        } else if (checkedId == R.id.rb_muscle_gain) {
            return "增肌运动";
        } else if (checkedId == R.id.rb_rehabilitation) {
            return "康复运动";
        }
        return "其他运动";
    }

    private String getSelectedExerciseAmount(Spinner spinner) {
        if (spinner == null) return "30";
        String selected = spinner.getSelectedItem().toString();
        return selected.replace("分钟", "");
    }

    private void createPlan(String name, String sportName, String detail, String exerciseAmount) {
        Integer userId = new SessionManager(this).getUserId();
        if (userId == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        TrainingPlan request = new TrainingPlan();
        request.setPlanName(name);
        request.setUserID(userId);
        request.setPlanType(0);
        request.setStartTime(startDateStr);
        request.setEndTime(endDateStr);
        request.setSportName(sportName);
        request.setExerciseAmount(exerciseAmount);
        request.setDetail(detail);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.createTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(CreateExercisePlanActivity.this, "计划创建成功", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    String msg = response.body() == null ? "计划创建失败" : response.body().getMessage();
                    Toast.makeText(CreateExercisePlanActivity.this, msg == null ? "计划创建失败" : msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(CreateExercisePlanActivity.this, "计划创建失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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