package com.example.myapplication;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.Result;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPlanActivity extends AppCompatActivity {
    private Integer planId;
    private String startDateStr = "";
    private String endDateStr = "";
    private TrainingPlan currentPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_plan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        planId = getIntent().getIntExtra("plan_id", -1);
        if (planId == -1) {
            String planIdStr = getIntent().getStringExtra("plan_id");
            if (planIdStr != null) {
                try {
                    planId = Integer.parseInt(planIdStr);
                } catch (NumberFormatException ignored) {}
            }
        }

        initViews();
        setupSpinner();
        if (planId != null && planId != -1) {
            loadPlanDetails();
        }
    }

    private void initViews() {
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

        Button cancelBtn = findViewById(R.id.btn_cancel);
        if (cancelBtn != null) {
            cancelBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        Button saveBtn = findViewById(R.id.btn_save);
        if (saveBtn != null) {
            saveBtn.setOnClickListener(v -> savePlan());
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

    private void loadPlanDetails() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        TrainingPlan request = new TrainingPlan();
        request.setPlanID(planId);

        apiService.getTrainingPlanById(request).enqueue(new Callback<Result<TrainingPlan>>() {
            @Override
            public void onResponse(Call<Result<TrainingPlan>> call, Response<Result<TrainingPlan>> response) {
                if (response.isSuccessful() && response.body() != null
                        && response.body().getCode() == 200 && response.body().getData() != null) {
                    currentPlan = response.body().getData();
                    populateForm(currentPlan);
                } else {
                    Toast.makeText(EditPlanActivity.this, "加载计划失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<TrainingPlan>> call, Throwable t) {
                Toast.makeText(EditPlanActivity.this, "加载失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateForm(TrainingPlan plan) {
        EditText planName = findViewById(R.id.et_plan_name);
        if (planName != null && plan.getPlanName() != null) {
            planName.setText(plan.getPlanName());
        }

        RadioGroup rgExerciseType = findViewById(R.id.rg_exercise_type);
        if (rgExerciseType != null && plan.getSportName() != null) {
            String sportName = plan.getSportName();
            if (sportName.contains("减脂")) {
                rgExerciseType.check(R.id.rb_fat_loss);
            } else if (sportName.contains("增肌")) {
                rgExerciseType.check(R.id.rb_muscle_gain);
            } else if (sportName.contains("康复")) {
                rgExerciseType.check(R.id.rb_rehabilitation);
            } else {
                rgExerciseType.check(R.id.rb_other);
            }
        }

        TextView tvStartDate = findViewById(R.id.tv_start_date);
        TextView tvEndDate = findViewById(R.id.tv_end_date);

        if (plan.getStartTime() != null) {
            startDateStr = plan.getStartTime().split("T")[0];
            if (tvStartDate != null) {
                tvStartDate.setText(startDateStr);
                tvStartDate.setHint("");
            }
        }

        if (plan.getEndTime() != null) {
            endDateStr = plan.getEndTime().split("T")[0];
            if (tvEndDate != null) {
                tvEndDate.setText(endDateStr);
                tvEndDate.setHint("");
            }
        }

        Spinner spExerciseAmount = findViewById(R.id.sp_exercise_amount);
        if (spExerciseAmount != null && plan.getExerciseAmount() != null) {
            try {
                int amount = Integer.parseInt(plan.getExerciseAmount());
                String amountStr = amount + "分钟";
                for (int i = 0; i < spExerciseAmount.getAdapter().getCount(); i++) {
                    if (spExerciseAmount.getAdapter().getItem(i).toString().equals(amountStr)) {
                        spExerciseAmount.setSelection(i);
                        break;
                    }
                }
            } catch (NumberFormatException ignored) {}
        }

        EditText etDetail = findViewById(R.id.et_detail);
        if (etDetail != null && plan.getDetail() != null) {
            etDetail.setText(plan.getDetail());
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
                        TextView tvStartDate = findViewById(R.id.tv_start_date);
                        if (tvStartDate != null) {
                            tvStartDate.setText(dateStr);
                            tvStartDate.setHint("");
                        }
                    } else {
                        endDateStr = dateStr;
                        TextView tvEndDate = findViewById(R.id.tv_end_date);
                        if (tvEndDate != null) {
                            tvEndDate.setText(dateStr);
                            tvEndDate.setHint("");
                        }
                    }
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void savePlan() {
        if (currentPlan == null) {
            Toast.makeText(this, "计划加载失败", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText planName = findViewById(R.id.et_plan_name);
        RadioGroup rgExerciseType = findViewById(R.id.rg_exercise_type);
        Spinner spExerciseAmount = findViewById(R.id.sp_exercise_amount);
        EditText etDetail = findViewById(R.id.et_detail);

        String name = planName != null ? planName.getText().toString().trim() : "";
        if (name.isEmpty()) {
            Toast.makeText(this, "请填写计划名称", Toast.LENGTH_SHORT).show();
            return;
        }

        String exerciseType = "其他运动";
        if (rgExerciseType != null) {
            int checkedId = rgExerciseType.getCheckedRadioButtonId();
            if (checkedId == R.id.rb_fat_loss) {
                exerciseType = "减脂运动";
            } else if (checkedId == R.id.rb_muscle_gain) {
                exerciseType = "增肌运动";
            } else if (checkedId == R.id.rb_rehabilitation) {
                exerciseType = "康复运动";
            }
        }

        if (startDateStr.isEmpty()) {
            Toast.makeText(this, "请选择开始日期", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endDateStr.isEmpty()) {
            Toast.makeText(this, "请选择结束日期", Toast.LENGTH_SHORT).show();
            return;
        }

        String exerciseAmountStr = "30";
        if (spExerciseAmount != null) {
            String selected = spExerciseAmount.getSelectedItem().toString();
            exerciseAmountStr = selected.replace("分钟", "");
        }

        String detail = etDetail != null ? etDetail.getText().toString().trim() : "";

        Integer userId = new SessionManager(this).getUserId();

        TrainingPlan request = new TrainingPlan();
        request.setPlanID(planId);
        request.setPlanName(name);
        request.setUserID(userId);
        request.setPlanType(currentPlan.getPlanType());
        request.setStartTime(startDateStr);
        request.setEndTime(endDateStr);
        request.setSportName(exerciseType);
        request.setExerciseAmount(exerciseAmountStr);
        request.setDetail(detail);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.updateTrainingPlan(request).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCode() == 200) {
                    Toast.makeText(EditPlanActivity.this, "计划已更新", Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(0, 0);
                } else {
                    String msg = response.body() != null ? response.body().getMessage() : "更新失败";
                    Toast.makeText(EditPlanActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Toast.makeText(EditPlanActivity.this, "更新失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}