package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editTextOldPassword, editTextNewPassword;
    private Button buttonChangePassword;
    private UserDatabaseHelper dbHelper;
    private TextView buttonCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);

        // Ánh xạ view nè.
        initializeViews();
        setupListeners();

    }

    private void changePassword() {
        // Lấy giá trị từ EditText
        String oldPassword = editTextOldPassword.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String passwordCurrent = UserManager.getInstance().getUser().getPassword();

        // Kiểm tra nếu các trường không bị trống
        if (TextUtils.isEmpty(oldPassword)) {
            Toast.makeText(this, "Please enter your old password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Least 6 chars
        if (newPassword.length() < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!passwordCurrent.equals(oldPassword)) {
            Toast.makeText(this, "Wrong old password.", Toast.LENGTH_SHORT).show();
            return;
        }


        dbHelper = new UserDatabaseHelper(this);
        Integer idUser = UserManager.getInstance().getUser().getId();
        dbHelper.open();
        Boolean isSuccess = dbHelper.changePassword(idUser,oldPassword, newPassword);

        if (isSuccess) {
            // Password changed successfully
            User currentUser = UserManager.getInstance().getUser();
            currentUser.setPassword(newPassword);

            Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show();
        } else {
            // Failed to change password
            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show();
        }

        dbHelper.close();
        finish();
    }

    private void goBackScreen() {
        finish();
    }


    private void initializeViews() {
        buttonCancel = findViewById(R.id.btnCancel);
        editTextOldPassword = findViewById(R.id.editTextOldPassword);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
    }

    private void setupListeners() {
        buttonChangePassword.setOnClickListener(v -> changePassword());
        buttonCancel.setOnClickListener(v -> goBackScreen());
    }
}
