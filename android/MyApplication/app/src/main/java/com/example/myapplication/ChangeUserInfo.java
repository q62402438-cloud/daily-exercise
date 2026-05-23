package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.model.OrdinaryUser;
import com.example.myapplication.model.Result;
import com.example.myapplication.model.User;
import com.example.myapplication.network.ApiService;
import com.example.myapplication.network.RetrofitClient;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeUserInfo extends AppCompatActivity {

    private static final String TAG = "ChangeUserInfo";

    private EditText etUserId, etUserName, etBirthday, etAge, etWeight;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private EditText etPhoneNumber, etUserMailbox;
    private Button btnSave, btnCancel;

    private OrdinaryUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_user_info);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
        loadUserInfo();
    }

    private void initViews() {
        etUserId = findViewById(R.id.etUserId);
        etUserName = findViewById(R.id.etUserName);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        etBirthday = findViewById(R.id.etBirthday);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);

        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etUserMailbox = findViewById(R.id.etUserMailbox);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        etUserId.setEnabled(false);
        etUserId.setFocusable(false);
        etUserId.setFocusableInTouchMode(false);

        etAge.setEnabled(false);
        etAge.setFocusable(false);
        etAge.setFocusableInTouchMode(false);

        etPhoneNumber.setEnabled(false);
    }

    private void setupListeners() {
        etBirthday.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveInfo());
        btnCancel.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });

        View backBtn = findViewById(R.id.btn_back);
        if (backBtn != null) {
            backBtn.setOnClickListener(v -> {
                finish();
                overridePendingTransition(0, 0);
            });
        }
    }

    private void loadUserInfo() {
        User user = getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "用户未登录", Toast.LENGTH_SHORT).show();
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
                        currentUser = result.getData();
                        fillData();
                    } else {
                        Toast.makeText(ChangeUserInfo.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangeUserInfo.this, "获取用户信息失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                Log.e(TAG, "加载用户信息失败", t);
                Toast.makeText(ChangeUserInfo.this, "网络请求失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillData() {
        if (currentUser == null) {
            return;
        }

        etUserId.setText(currentUser.getUserID() != null ? String.valueOf(currentUser.getUserID()) : "");
        etUserName.setText(currentUser.getUserName() != null ? currentUser.getUserName() : "");
        etPhoneNumber.setText(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "");
        etUserMailbox.setText(currentUser.getUserMailbox() != null ? currentUser.getUserMailbox() : "");

        String gender = currentUser.getGender();
        if (gender != null) {
            if ("男".equals(gender)) {
                rbMale.setChecked(true);
            } else if ("女".equals(gender)) {
                rbFemale.setChecked(true);
            }
        }

        String birthday = currentUser.getBirthday();
        if (birthday != null && !birthday.isEmpty()) {
            etBirthday.setText(birthday);
            int age = calculateAge(birthday);
            etAge.setText(String.valueOf(age));
            currentUser.setAge(age);
        }

        if (currentUser.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUser.getWeight()));
        }
    }

    private void saveInfo() {
        if (currentUser == null) {
            Toast.makeText(this, "用户信息未加载", Toast.LENGTH_SHORT).show();
            return;
        }

        String newName = etUserName.getText().toString().trim();
        String newEmail = etUserMailbox.getText().toString().trim();

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String newGender = "";
        if (selectedGenderId == R.id.rbMale) {
            newGender = "男";
        } else if (selectedGenderId == R.id.rbFemale) {
            newGender = "女";
        }

        String newBirthday = etBirthday.getText().toString().trim();
        String newWeight = etWeight.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setUserName(newName);
        currentUser.setUserMailbox(newEmail);
        currentUser.setGender(newGender);
        currentUser.setBirthday(newBirthday);

        if (!TextUtils.isEmpty(newWeight)) {
            try {
                currentUser.setWeight(Float.parseFloat(newWeight));
            } catch (NumberFormatException e) {
                Toast.makeText(this, "体重格式错误", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!TextUtils.isEmpty(newBirthday)) {
            int age = calculateAge(newBirthday);
            currentUser.setAge(age);
        }

        btnSave.setEnabled(false);
        btnSave.setText("保存中...");

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Call<Result<String>> call = apiService.updateUser(currentUser);

        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                btnSave.setEnabled(true);
                btnSave.setText("保存");

                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        Toast.makeText(ChangeUserInfo.this, "用户信息修改成功！", Toast.LENGTH_SHORT).show();
                        finish();
                        overridePendingTransition(0, 0);
                    } else {
                        Toast.makeText(ChangeUserInfo.this, result.getMessage() != null ? result.getMessage() : "修改失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangeUserInfo.this, "修改失败", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                btnSave.setEnabled(true);
                btnSave.setText("保存");
                Log.e(TAG, "保存用户信息失败", t);
                Toast.makeText(ChangeUserInfo.this, "网络请求失败：" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String existingBirthday = etBirthday.getText().toString().trim();
        if (existingBirthday != null && !existingBirthday.isEmpty()) {
            try {
                String[] parts = existingBirthday.split("-");
                if (parts.length == 3) {
                    year = Integer.parseInt(parts[0]);
                    month = Integer.parseInt(parts[1]) - 1;
                    day = Integer.parseInt(parts[2]);
                }
            } catch (Exception e) {
                Log.w(TAG, "解析现有生日失败");
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String birthday = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    etBirthday.setText(birthday);
                    int age = calculateAge(birthday);
                    etAge.setText(String.valueOf(age));
                    if (currentUser != null) {
                        currentUser.setBirthday(birthday);
                        currentUser.setAge(age);
                    }
                },
                year,
                month,
                day
        );

        Calendar maxDate = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -150);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private int calculateAge(String birthday) {
        try {
            String[] parts = birthday.split("-");
            if (parts.length == 3) {
                int birthYear = Integer.parseInt(parts[0]);
                int birthMonth = Integer.parseInt(parts[1]);
                int birthDay = Integer.parseInt(parts[2]);

                Calendar today = Calendar.getInstance();
                int currentYear = today.get(Calendar.YEAR);
                int currentMonth = today.get(Calendar.MONTH) + 1;
                int currentDay = today.get(Calendar.DAY_OF_MONTH);

                int age = currentYear - birthYear;

                if (currentMonth < birthMonth ||
                        (currentMonth == birthMonth && currentDay < birthDay)) {
                    age--;
                }

                return Math.max(0, age);
            }
        } catch (Exception e) {
            Log.e(TAG, "计算年龄失败: " + e.getMessage());
        }
        return 0;
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

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}