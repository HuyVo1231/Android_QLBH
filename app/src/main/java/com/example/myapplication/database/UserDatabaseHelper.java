package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.model.User;

public class UserDatabaseHelper {
    private SQLiteDatabase database;
    private ConnectDatabase dbHelper;

    public UserDatabaseHelper(Context context) {
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

    public long addUser(User user) {
        // Check if the username or phone already exists in the database
        if (isUsernameExist(user.getUsername())) {
            Log.e("Database", "Username already exists");
            return -1;
        }
        if (isPhoneExist(user.getPhone())) {
            Log.e("Database", "Phone number already exists");
            return -2;
        }

        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("full_name", user.getFullName());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());
        values.put("imageUrl", user.getImageUrl());
        values.put("isAdmin", user.isAdmin() ? 1 : 0); // Store 1 for true, 0 for false

        long rowId = database.insert("user", null, values);

        if (rowId == -1) {
            Log.e("Database", "Failed to add user");
        } else {
            Log.d("Database", "User added with rowId: " + rowId);
        }

        return rowId;
    }

    // Public method to check if the username exists
    public boolean checkIfUsernameExists(String username) {
        return isUsernameExist(username);
    }

    // Check if the username already exists
    private boolean isUsernameExist(String username) {
        Cursor cursor = null;
        try {
            cursor = database.query("user",
                    new String[]{"username"},
                    "username = ?",
                    new String[]{username},
                    null, null, null);
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("Database", "Error checking username", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Check if the phone already exists
    public boolean isPhoneExist(String phone) {
        Cursor cursor = null;
        try {
            cursor = database.query("user",
                    new String[]{"phone"},
                    "phone = ?",
                    new String[]{phone},
                    null, null, null);
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("Database", "Error checking phone", e);
            return false;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    // Login a user by checking phone and password
    public User login(String phone, String password) {
        Cursor cursor = null;
        try {
            cursor = database.query("user",
                    new String[]{"id", "username", "password", "full_name", "email", "phone", "address", "imageUrl", "isAdmin"},
                    "phone = ? AND password = ?",
                    new String[]{phone, password},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                String fullName = cursor.getString(cursor.getColumnIndexOrThrow("full_name"));
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl"));
                boolean isAdmin = cursor.getInt(cursor.getColumnIndexOrThrow("isAdmin")) == 1;

                return new User(id, username, password, fullName, phone, email, address, imageUrl, isAdmin);
            }
        } catch (Exception e) {
            Log.e("Database", "Error during login", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return null; // Return null if no user found
    }

    // Method to change password
    public boolean changePassword(Integer id, String oldPassword, String newPassword) {
        Cursor cursor = null;
        try {
            cursor = database.query("user",
                    new String[]{"id", "password"},
                    "id = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                if (storedPassword.equals(oldPassword)) {
                    ContentValues values = new ContentValues();
                    values.put("password", newPassword);
                    int rowsAffected = database.update("user", values, "id = ?", new String[]{String.valueOf(id)});
                    return rowsAffected > 0;
                }
            }
        } catch (Exception e) {
            Log.e("Database", "Error changing password", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        return false;
    }

    // Method to update user information
    public boolean updateUser(User user) {
        if (user.getId() == null) {
            Log.e("Database", "User ID is null");
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("full_name", user.getFullName());
        values.put("email", user.getEmail());
        values.put("phone", user.getPhone());
        values.put("address", user.getAddress());
        values.put("imageUrl", user.getImageUrl());

        try {
            int rowsAffected = database.update("user", values, "id = ?", new String[]{String.valueOf(user.getId())});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e("Database", "Error updating user", e);
            return false;
        }
    }
}
