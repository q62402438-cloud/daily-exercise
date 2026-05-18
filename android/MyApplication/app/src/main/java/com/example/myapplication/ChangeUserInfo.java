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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.OrdinaryUser;

import java.util.Calendar;

public class ChangeUserInfo extends AppCompatActivity {

    private static final String TAG = "ChangeUserInfo";

    private EditText etUserId, etUserName, etBirthday, etAge, etWeight;
    private AutoCompleteTextView etGender;
    private EditText etPhoneNumber, etUserMailbox;
    private Button btnSave, btnCancel;

    private OrdinaryUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_info);

        initViews();
        receiveUserData();
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
        etGender.setAdapter(adapter);
    }

    private void receiveUserData() {
        Intent intent = getIntent();
        if (intent != null) {
            currentUser = (OrdinaryUser) intent.getSerializableExtra("user_data");
        }

        if (currentUser == null) {
            currentUser = new OrdinaryUser();
            currentUser.setUserID(1);
            currentUser.setUserName("运动达人");
            currentUser.setPhoneNumber("13800138000");
            currentUser.setUserMailbox("user@example.com");
            currentUser.setGender("男");
            currentUser.setBirthday("1995-06-15");
            currentUser.setAge(29);
            currentUser.setWeight(70.5f);
        }

        fillData();
    }

    private void fillData() {
        etUserId.setText(String.valueOf(currentUser.getUserID()));
        etUserName.setText(currentUser.getUserName());
        etPhoneNumber.setText(currentUser.getPhoneNumber());
        etUserMailbox.setText(currentUser.getUserMailbox());
        etGender.setText(currentUser.getGender());
        etBirthday.setText(currentUser.getBirthday());

        if (currentUser.getAge() != null) {
            etAge.setText(String.valueOf(currentUser.getAge()));
        }

        if (currentUser.getWeight() != null) {
            etWeight.setText(String.valueOf(currentUser.getWeight()));
        }
    }

    private void setupListeners() {
        etBirthday.setOnClickListener(v -> showDatePickerDialog());
        btnSave.setOnClickListener(v -> saveInfo());
        btnCancel.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0);
        });
    }

    private void saveInfo() {
        String newName = etUserName.getText().toString().trim();
        String newPhone = etPhoneNumber.getText().toString().trim();
        String newEmail = etUserMailbox.getText().toString().trim();
        String newGender = etGender.getText().toString().trim();
        String newBirthday = etBirthday.getText().toString().trim();
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
            currentUser.setWeight(
                    TextUtils.isEmpty(newWeight) ? null : Float.parseFloat(newWeight)
            );
        } catch (Exception e) {
            showToast("体重格式错误");
            return;
        }

        showToast("用户信息修改成功！");
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updated_user", currentUser);
        setResult(RESULT_OK, resultIntent);
        finish();
        overridePendingTransition(0, 0);
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }
}