package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.model.Admin;
import com.example.myapplication.model.User;

import java.util.ArrayList;

public class AdminDatabaseHelper {
    private SQLiteDatabase database;
    private ConnectDatabase dbHelper;

    public AdminDatabaseHelper(Context context) {
        dbHelper = new ConnectDatabase(context);
    }

    // Open database connection
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Close database connection
    public void close() {
        dbHelper.close();
    }

    public long addUser(Admin admin) {

        Log.d("AdminInfo", "New Admin: " + admin);
        if (isUsernameExist(admin.getUsername())) {  // Check username of the admin
            Log.e("Database", "Username already exists");
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put("username", admin.getUsername());
        values.put("password", admin.getPassword());
        values.put("full_name", admin.getFullName());
        values.put("email", admin.getEmail());
        values.put("role", admin.getRole());

        long rowId = database.insert("admin", null, values);

        if (rowId == -1) {
            Log.e("Database", "Failed to add admin");
        } else {
            Log.d("Database", "Admin added with rowId: " + rowId);
        }

        return rowId;
    }


    // Public method to check if the username exists
    public boolean checkIfUsernameExists(String username) {
        return isUsernameExist(username);
    }

    // Check if the username already exists
    private boolean isUsernameExist(String username) {
        Cursor cursor = database.query("admin",
                new String[]{"username"},
                "username = ?",
                new String[]{username},
                null, null, null);

        boolean exists = false;
        if (cursor != null) {
            exists = cursor.moveToFirst();
            cursor.close();
        }

        return exists;  // True if username exists, false otherwise
    }

    // Check if the phone already exists
    private boolean isPhoneExist(String phone) {
        Cursor cursor = database.query("admin",
                new String[]{"phone"},
                "phone = ?",
                new String[]{phone},
                null, null, null);

        boolean exists = false;
        if (cursor != null) {
            exists = cursor.moveToFirst();
            cursor.close();
        }

        return exists;  // True if phone exists, false otherwise
    }


    // Login a user by checking username and password
    public boolean login(String username, String password) {
        // Query the database for the user with the given username and password
        Cursor cursor = database.query("admin", // Table name
                new String[]{"id"}, // Only need to check if a record exists
                "username = ? AND password = ?", // WHERE clause
                new String[]{username, password}, // WHERE arguments
                null, null, null);

        // Check if a matching user was found
        boolean isLoggedIn = false;
        if (cursor != null && cursor.moveToFirst()) {
            isLoggedIn = true; // Matching user found
        }

        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }

        return isLoggedIn; // Return true if login is successful
    }



}
