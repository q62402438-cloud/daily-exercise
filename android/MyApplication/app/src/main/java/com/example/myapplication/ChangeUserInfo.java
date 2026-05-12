package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

    private TextView tvWelcome;
    private EditText etUserId, etUserName, etGender, etBirthday, etAge, etWeight;
    private EditText etPhoneNumber, etUserMailbox;
    private Button btnSave, btnCancel;

    private OrdinaryUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        initViews();
        receiveUserData();
        fetchUserInfoFromServer();
        setupListeners();
    }

    private void initViews() {
        etUserId = findViewById(R.id.etUserId);
        etUserName = findViewById(R.id.etUserName);
        etGender = findViewById(R.id.etGender);
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

        String[] genders = {"男", "女"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                genders
        );
        ((AutoCompleteTextView) etGender).setAdapter(adapter);
    }

    /**
     * 接收 Intent 中的用户数据
     */
    private void receiveUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentUser = (OrdinaryUser) intent.getSerializableExtra("user_data");
        }

        if (currentUser == null) {
            Log.e(TAG, "currentUser is null !!!");
            showToast("用户信息获取失败");
            finish();
            return;
        }

        Log.d(TAG, "接收到的用户ID：" + currentUser.getUserID());
    }

    private void fetchUserInfoFromServer() {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        User user = new User();
        user.setUserID(currentUser.getUserID());

        Call<Result<OrdinaryUser>> call = apiService.getUserInfo(user);
        call.enqueue(new Callback<Result<OrdinaryUser>>() {
            @Override
            public void onResponse(Call<Result<OrdinaryUser>> call, Response<Result<OrdinaryUser>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Result<OrdinaryUser> result = response.body();
                        Log.d(TAG, "后端返回的业务码: " + result.getCode());
                        Log.d(TAG, "后端返回的消息: " + result.getMessage());
                        Log.d(TAG, "后端返回的数据: " + result.getData());
                        
                        if (result.getCode() == 200) {
                            if (result.getData() != null) {
                                currentUser = result.getData();
                                fillData();
                            } else {
                                showToast("获取用户信息失败：数据为空");
                                Log.e(TAG, "获取用户信息失败：数据为空");
                            }
                        } else {
                            showToast("获取用户信息失败：" + result.getMessage());
                            Log.e(TAG, "获取用户信息失败：业务码=" + result.getCode() + ", 消息=" + result.getMessage());
                        }
                    } else {
                        showToast("获取用户信息失败：响应体为空");
                        Log.e(TAG, "获取用户信息失败：响应体为空");
                    }
                } else {
                    showToast("获取用户信息失败，状态码：" + response.code());
                    Log.e(TAG, "获取用户信息失败，HTTP状态码：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Result<OrdinaryUser>> call, Throwable t) {
                showToast("网络请求失败：" + t.getMessage());
                Log.e(TAG, "网络请求失败：", t);
            }
        });
    }

    /**
     * 填充界面数据（父类 + 子类字段）
     */
    private void fillData() {
        etUserId.setText(String.valueOf(currentUser.getUserID()));
        etUserName.setText(currentUser.getUserName());
        etPhoneNumber.setText(currentUser.getPhoneNumber());
        etUserMailbox.setText(currentUser.getUserMailbox());
        etGender.setText(currentUser.getGender());
        etBirthday.setText(currentUser.getBirthday());

        String birthday = currentUser.getBirthday();
        if (birthday != null && !birthday.isEmpty()) {
            int age = calculateAge(birthday);
            etAge.setText(String.valueOf(age));
            currentUser.setAge(age);
        }

        if (currentUser.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUser.getWeight()));
        }
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

    /**
     * 绑定事件
     */
    private void setupListeners() {
        etBirthday.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveInfo());
        btnCancel.setOnClickListener(v -> finish());
    }

    /**
     * 保存用户信息
     */
    private void saveInfo() {
        String newName = etUserName.getText().toString().trim();
        String newPhone = etPhoneNumber.getText().toString().trim();
        String newEmail = etUserMailbox.getText().toString().trim();
        String newGender = etGender.getText().toString().trim();
        String newBirthday = etBirthday.getText().toString().trim();
        String newAge = etAge.getText().toString().trim();
        String newWeight = etWeight.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            showToast("用户名不能为空");
            return;
        }

        currentUser.setUserName(newName);
        currentUser.setPhoneNumber(newPhone);
        currentUser.setUserMailbox(newEmail);
        currentUser.setGender(newGender);
        currentUser.setBirthday(newBirthday);

        try {
            currentUser.setAge(
                    TextUtils.isEmpty(newAge) ? null : Integer.parseInt(newAge)
            );
            currentUser.setWeight(
                    TextUtils.isEmpty(newWeight) ? null : Float.parseFloat(newWeight)
            );
        } catch (Exception e) {
            showToast("年龄或体重格式错误");
            return;
        }

        ApiService apiService =
                RetrofitClient.getInstance().create(ApiService.class);

        Call<Result<String>> call = apiService.updateUser(currentUser);

        call.enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        showToast("用户信息修改成功！");
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("updated_user", currentUser);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    } else {
                        showToast(result.getMessage());
                    }
                } else {
                    showToast("更新失败，状态码：" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                showToast("网络请求失败：" + t.getMessage());
            }
        });
    }

    /**
     * 日期选择器
     */
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

        android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String birthday = String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth);
                    etBirthday.setText(birthday);
                    int age = calculateAge(birthday);
                    etAge.setText(String.valueOf(age));
                    currentUser.setBirthday(birthday);
                    currentUser.setAge(age);
                },
                year,
                month,
                day
        );

        datePickerDialog.getDatePicker().setCalendarViewShown(false);
        datePickerDialog.getDatePicker().setSpinnersShown(true);

        Calendar maxDate = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -150);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

        datePickerDialog.show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}