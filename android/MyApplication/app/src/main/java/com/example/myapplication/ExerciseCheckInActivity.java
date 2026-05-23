package com.example.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.ExerciseRecord;
import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.SportsEvent;
import com.example.myapplication.model.TrainingPlan;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExerciseCheckInActivity extends AppCompatActivity {

    private List<SportsEvent> sportsEvents = new ArrayList<>();
    private SportsEvent selectedEvent = null;
    private float userWeight = 0;

    private TextView tvDate;
    private TextView tvStartTime;
    private TextView tvEndTime;
    private TextView tvDuration;
    private TextView tvCalorie;
    private TextView tvAmountUnit;
    private EditText etExerciseAmount;
    private Spinner spSportType;
    private EditText etUserWeight;
    private LinearLayout layoutExerciseAmount;

    private Integer planId = null;
    private String planName = null;
    private String planStartDate = null;
    private String planEndDate = null;
    private int dailyExerciseMinutes = 30;
    private boolean isPlanSelected = false;
    private List<TrainingPlan> inProgressPlans = new ArrayList<>();

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

        initViews();
        loadUserInfo();
        loadSportsEvents();
        loadUserPlans();
    }

    private void initViews() {
        ImageButton backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }

        tvDate = findViewById(R.id.tv_date);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvCalorie = findViewById(R.id.tv_calorie);
        tvAmountUnit = findViewById(R.id.tv_amount_unit);
        etExerciseAmount = findViewById(R.id.et_exercise_amount);
        spSportType = findViewById(R.id.sp_sport_type);
        etUserWeight = findViewById(R.id.et_user_weight);
        layoutExerciseAmount = findViewById(R.id.layout_exercise_amount);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(today);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            planId = bundle.getInt("plan_id", -1);
            if (planId == -1) planId = null;
            planName = bundle.getString("plan_name", null);
            planStartDate = bundle.getString("start_date", null);
            planEndDate = bundle.getString("end_date", null);
            String dailyExerciseStr = bundle.getString("daily_exercise", "30");
            try {
                dailyExerciseMinutes = Integer.parseInt(dailyExerciseStr);
            } catch (NumberFormatException ignored) {}
        }

        tvDate.setOnClickListener(v -> showDatePicker());
        tvStartTime.setOnClickListener(v -> showTimePicker(true));
        tvEndTime.setOnClickListener(v -> showTimePicker(false));

        etUserWeight.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                calculateCalorie();
            }
        });

        spSportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int planCount = inProgressPlans.size();

                if (position == 0) {
                    isPlanSelected = false;
                    selectedEvent = null;
                    planId = null;
                    planName = null;
                    dailyExerciseMinutes = 30;
                    if (layoutExerciseAmount != null) {
                        layoutExerciseAmount.setVisibility(View.VISIBLE);
                    }
                    tvAmountUnit.setText("个");
                } else if (position <= planCount) {
                    TrainingPlan selectedPlan = inProgressPlans.get(position - 1);
                    isPlanSelected = true;
                    selectedEvent = null;
                    planId = selectedPlan.getPlanID();
                    planName = selectedPlan.getPlanName();
                    String exerciseAmount = selectedPlan.getExerciseAmount();
                    if (exerciseAmount != null && !exerciseAmount.isEmpty()) {
                        dailyExerciseMinutes = (int) Float.parseFloat(exerciseAmount);
                    } else {
                        dailyExerciseMinutes = 30;
                    }
                    if (layoutExerciseAmount != null) {
                        layoutExerciseAmount.setVisibility(View.GONE);
                    }
                    tvAmountUnit.setText("分钟");
                } else {
                    isPlanSelected = false;
                    selectedEvent = sportsEvents.get(position - planCount - 1);
                    planId = null;
                    planName = null;
                    dailyExerciseMinutes = 30;
                    if (layoutExerciseAmount != null) {
                        layoutExerciseAmount.setVisibility(View.VISIBLE);
                    }
                    updateAmountUnit(selectedEvent.getSportName());
                }
                calculateCalorie();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedEvent = null;
            }
        });

        Button finishBtn = findViewById(R.id.btn_finish);
        if (finishBtn != null) {
            finishBtn.setOnClickListener(v -> submitCheckin());
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            String date = String.format("%04d-%02d-%02d", year1, month1 + 1, dayOfMonth);
            tvDate.setText(date);
        }, year, month, day);
        dialog.show();
    }

    private void showTimePicker(boolean isStartTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            String time = String.format("%02d:%02d", hourOfDay, minute1);
            if (isStartTime) {
                tvStartTime.setText(time);
                if (isPlanSelected) {
                    int startTotal = hourOfDay * 60 + minute1;
                    int endTotal = startTotal + dailyExerciseMinutes;
                    int endHour = (endTotal / 60) % 24;
                    int endMinute = endTotal % 60;
                    String endTime = String.format("%02d:%02d", endHour, endMinute);
                    tvEndTime.setText(endTime);
                    calculateDuration();
                } else {
                    calculateDuration();
                }
            } else {
                tvEndTime.setText(time);
                calculateDuration();
            }
        }, hour, minute, true);
        dialog.show();
    }

    private void calculateDuration() {
        String start = tvStartTime.getText().toString();
        String end = tvEndTime.getText().toString();

        if (start.isEmpty() || end.isEmpty()) {
            tvDuration.setText("0 分钟");
            return;
        }

        try {
            String[] startParts = start.split(":");
            String[] endParts = end.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMin = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMin = Integer.parseInt(endParts[1]);

            int startTotal = startHour * 60 + startMin;
            int endTotal = endHour * 60 + endMin;

            if (endTotal < startTotal) {
                endTotal += 24 * 60;
            }

            int duration = endTotal - startTotal;
            tvDuration.setText(duration + " 分钟");
            calculateCalorie();
        } catch (Exception e) {
            tvDuration.setText("0 分钟");
        }
    }

    private void updateAmountUnit(String sportName) {
        if (sportName == null || sportName.isEmpty()) {
            tvAmountUnit.setText("个");
            return;
        }

        switch (sportName) {
            case "慢走":
            case "快走":
            case "慢跑":
            case "跑步":
            case "骑行":
                tvAmountUnit.setText("公里");
                break;
            case "游泳":
                tvAmountUnit.setText("米");
                break;
            case "跳绳":
                tvAmountUnit.setText("个");
                break;
            case "力量训练":
                tvAmountUnit.setText("组");
                break;
            default:
                tvAmountUnit.setText("个");
        }
    }

    private void calculateCalorie() {
        if (isPlanSelected) {
            tvCalorie.setText("");
            return;
        }

        if (selectedEvent == null) {
            tvCalorie.setText("");
            return;
        }

        String weightStr = etUserWeight.getText().toString();
        if (weightStr.isEmpty()) {
            tvCalorie.setText("");
            return;
        }

        String durationStr = tvDuration.getText().toString().replace(" 分钟", "");
        if (durationStr.isEmpty()) {
            tvCalorie.setText("");
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            int duration = Integer.parseInt(durationStr);
            float avgCalorie = selectedEvent.getAverageCalorie();

            int calorie = Math.round((avgCalorie * weight / 70) * duration);
            tvCalorie.setText(String.valueOf(calorie));
        } catch (Exception e) {
            tvCalorie.setText("");
        }
    }

    private void loadUserInfo() {
        User user = getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<OrdinaryUser>> call = apiService.getUserInfo(user);

        call.enqueue(new Callback<Result<OrdinaryUser>>() {
            @Override
            public void onResponse(Call<Result<OrdinaryUser>> call, Response<Result<OrdinaryUser>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<OrdinaryUser> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        OrdinaryUser ordinaryUser = result.getData();
                        if (ordinaryUser.getWeight() != null) {
                            userWeight = ordinaryUser.getWeight();
                            etUserWeight.setText(String.valueOf(userWeight));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                Log.e("ExerciseCheckIn", "加载用户信息失败", t);
            }
        });
    }

    private void loadSportsEvents() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<SportsEvent>>> call = apiService.getAllSportsEvents();

        call.enqueue(new Callback<Result<List<SportsEvent>>>() {
            @Override
            public void onResponse(Call<Result<List<SportsEvent>>> call, Response<Result<List<SportsEvent>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<SportsEvent>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        sportsEvents = result.getData();
                        if (sportsEvents != null && !sportsEvents.isEmpty()) {
                            setupSpinner();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<SportsEvent>>> call, Throwable t) {
                Log.e("ExerciseCheckIn", "加载运动项目失败", t);
                Toast.makeText(ExerciseCheckInActivity.this, "加载运动项目失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserPlans() {
        User user = getCurrentUser();
        if (user == null) {
            return;
        }

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<List<TrainingPlan>>> call = apiService.getTrainingPlansByUser(user);

        call.enqueue(new Callback<Result<List<TrainingPlan>>>() {
            @Override
            public void onResponse(Call<Result<List<TrainingPlan>>> call, Response<Result<List<TrainingPlan>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<TrainingPlan>> result = response.body();
                    if (result.getCode() == 200 && result.getData() != null) {
                        inProgressPlans.clear();
                        for (TrainingPlan plan : result.getData()) {
                            Integer planType = plan.getPlanType();
                            int executionDigit = planType != null ? (planType / 10) % 10 : 0;
                            if (executionDigit == 1) {
                                inProgressPlans.add(plan);
                            }
                        }
                        if (!inProgressPlans.isEmpty()) {
                            setupSpinner();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Result<List<TrainingPlan>>> call, Throwable t) {
                Log.e("ExerciseCheckIn", "加载用户计划失败", t);
            }
        });
    }

    private void setupSpinner() {
        List<String> items = new ArrayList<>();
        items.add("请选择运动项目");

        for (TrainingPlan plan : inProgressPlans) {
            String planExerciseAmount = plan.getExerciseAmount() != null ? plan.getExerciseAmount() : "30";
            items.add(plan.getPlanName() + " (计划:" + planExerciseAmount + "分钟)");
        }

        for (SportsEvent event : sportsEvents) {
            items.add(event.getSportName() + " (" + event.getAverageCalorie() + " kcal/小时)");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSportType.setAdapter(adapter);
    }

    private User getCurrentUser() {
        SessionManager sessionManager = new SessionManager(this);
        Integer userId = sessionManager.getUserId();
        if (userId == null) {
            return null;
        }
        User user = new User();
        user.setUserID(userId);
        return user;
    }

    private void submitCheckin() {
        User user = getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = tvDate.getText().toString();
        String startTime = tvStartTime.getText().toString();
        String endTime = tvEndTime.getText().toString();

        if (startTime.isEmpty()) {
            Toast.makeText(this, "请选择开始时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (endTime.isEmpty()) {
            Toast.makeText(this, "请选择结束时间", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isPlanSelected) {
            submitPlanCheckin(user, date, startTime, endTime);
        } else {
            submitNormalCheckin(user, date, startTime, endTime);
        }
    }

    private void submitPlanCheckin(User user, String date, String startTime, String endTime) {
        try {
            ExerciseRecord record = new ExerciseRecord();
            record.setUserID(user.getUserID());
            record.setSportsDate(date);
            record.setPlanID(planId);
            record.setEventID(planId);
            record.setSportName(planName);
            record.setStartTime(date + " " + startTime + ":00");
            record.setEndTime(date + " " + endTime + ":00");
            record.setExerciseDuration(dailyExerciseMinutes);
            record.setExerciseAmount((float) dailyExerciseMinutes);
            record.setCalorie(0);
            record.setRecordType(1);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<Result<String>> call = apiService.addExerciseRecord(record);

            call.enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Result<String> result = response.body();
                        if (result.getCode() == 200) {
                            Toast.makeText(ExerciseCheckInActivity.this, "计划打卡成功！\n\n运动日期：" + date + "\n计划名称：" + planName + "\n开始时间：" + startTime + "\n结束时间：" + endTime + "\n运动时长：" + dailyExerciseMinutes + " 分钟", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ExerciseCheckInActivity.this, result.getMessage() != null ? result.getMessage() : "打卡失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExerciseCheckInActivity.this, "打卡失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Log.e("ExerciseCheckIn", "打卡失败", t);
                    Toast.makeText(ExerciseCheckInActivity.this, "打卡失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "数据格式错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitNormalCheckin(User user, String date, String startTime, String endTime) {
        if (selectedEvent == null) {
            Toast.makeText(this, "请选择运动项目", Toast.LENGTH_SHORT).show();
            return;
        }

        String amountStr = etExerciseAmount.getText().toString();
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "请输入运动数量", Toast.LENGTH_SHORT).show();
            return;
        }

        String calorieStr = tvCalorie.getText().toString();
        if (calorieStr.isEmpty()) {
            Toast.makeText(this, "请先输入体重计算卡路里", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ExerciseRecord record = new ExerciseRecord();
            record.setUserID(user.getUserID());
            record.setSportsDate(date);
            record.setEventID(selectedEvent.getEventID());
            record.setStartTime(date + " " + startTime + ":00");
            record.setEndTime(date + " " + endTime + ":00");
            record.setExerciseDuration(Integer.parseInt(tvDuration.getText().toString().replace(" 分钟", "")));
            record.setExerciseAmount(Float.parseFloat(amountStr));
            record.setCalorie(Integer.parseInt(calorieStr));
            record.setRecordType(0);

            ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
            Call<Result<String>> call = apiService.addExerciseRecord(record);

            call.enqueue(new Callback<Result<String>>() {
                @Override
                public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Result<String> result = response.body();
                        if (result.getCode() == 200) {
                            Toast.makeText(ExerciseCheckInActivity.this, "打卡成功！\n\n运动日期：" + date + "\n运动项目：" + selectedEvent.getSportName() + "\n开始时间：" + startTime + "\n结束时间：" + endTime + "\n运动时长：" + tvDuration.getText(), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(ExerciseCheckInActivity.this, result.getMessage() != null ? result.getMessage() : "打卡失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ExerciseCheckInActivity.this, "打卡失败", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Result<String>> call, Throwable t) {
                    Log.e("ExerciseCheckIn", "打卡失败", t);
                    Toast.makeText(ExerciseCheckInActivity.this, "打卡失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "数据格式错误", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}