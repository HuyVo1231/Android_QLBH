package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.AdminDatabaseHelper;
import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

public class AdminActivityLogin extends AppCompatActivity {

    private EditText editTextPhone, editTextPassword;
    private Button buttonLogin;
    private UserDatabaseHelper databaseHelper;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_login);

        textViewRegister = findViewById(R.id.textViewRegister);
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivityLogin.this, AdminActivitySignup.class);
                startActivity(intent);
            }
        });

        // Initialize UI components
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        // Initialize DatabaseHelper
        databaseHelper = new UserDatabaseHelper(this);

        // Set onClickListener for the Login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        // Get user input
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both phone number and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra số điện thoại hợp lệ
        if (!phone.matches("[0-9]+") || phone.length() != 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra mật khẩu (tối thiểu 6 ký tự)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }


        databaseHelper.open();
        // Check login in the database
        User user = databaseHelper.login(phone, password);

        if (user != null) {
            if(user.isAdmin()) {
                // Đưa info vào UserManger.
                UserManager.getInstance().setUser(user);
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                // Chuyển màn hình.
                Intent intent = new Intent(this, AdminActivityMain.class);
                startActivity(intent);
                finish();
            }
            else {
                Toast.makeText(this, "Tài khoản của bạn không phải là Admin!", Toast.LENGTH_SHORT).show();

            }
        } else {
            // Failed login
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }

    }
}
