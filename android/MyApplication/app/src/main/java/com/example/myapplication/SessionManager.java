package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREFS_NAME = "daily_exercise_session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_TYPE = "user_type";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveUser(Integer userId, String userName, Integer userType) {
        preferences.edit()
                .putInt(KEY_USER_ID, userId == null ? -1 : userId)
                .putString(KEY_USER_NAME, userName == null ? "" : userName)
                .putInt(KEY_USER_TYPE, userType == null ? 1 : userType)
                .apply();
    }

    public Integer getUserId() {
        int userId = preferences.getInt(KEY_USER_ID, -1);
        return userId > 0 ? userId : null;
    }

    public String getUserName() {
        return preferences.getString(KEY_USER_NAME, "");
    }

    public Integer getUserType() {
        return preferences.getInt(KEY_USER_TYPE, 1);
    }

    public boolean isLoggedIn() {
        return getUserId() != null;
    }

    public void clear() {
        preferences.edit().clear().apply();
    }
}
