package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferncesClass {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String REMEMBER_ME = "remember_me";
    private static final String PREFS_NAME = "LoginDetail";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SharedPreferncesClass(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();
    }

    // Lưu thông tin đăng nhập
    public void saveLoginDetails(String userName, String password) {
        editor.putString(USERNAME, userName);
        editor.putString(PASSWORD, password);
        editor.apply();
    }

    // Xóa thông tin đăng nhập
    public void clearLoginDetails() {
        editor.remove(USERNAME);
        editor.remove(PASSWORD);
        editor.apply();
    }

    // Lưu trạng thái checkbox "Remember Me"
    public void setRememberMe(boolean isChecked) {
        editor.putBoolean(REMEMBER_ME, isChecked);
        editor.apply();
    }

    public boolean getRememberMe() {
        return sharedPreferences.getBoolean(REMEMBER_ME, false);
    }


    public boolean isLoggedIn() {
        return !getUserName().isEmpty();
    }

    // Lấy username
    public String getUserName() {
        return sharedPreferences.getString(USERNAME, "");
    }

    // Lấy password
    public String getPassword() {
        return sharedPreferences.getString(PASSWORD, "");
    }
}
