package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferncesClass {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PREFS_NAME = "LoginDetail";
    private Context context;

    SharedPreferncesClass(Context context) {
        this.context = context;
    }

    public void saveLoginDetails(String userName, String password) {
        SharedPreferences sharedPre = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPre.edit();
        editor.putString(USERNAME, userName);
        editor.putString(PASSWORD, password);
        editor.commit();
    }

    // Get username
    public String getUserName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME, "");
    }

    // Get password
    public String getPassword() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD, "");
    }


}
