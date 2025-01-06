package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.ChangePasswordActivity;
import com.example.myapplication.R;
import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;

public class AdminActivitySignup extends AppCompatActivity {

    private EditText editTextNameOwner, editTextNameRestaurant, editTextName, editTextPhone, editTextPassword;
    private Button btnSignup;
    private UserDatabaseHelper databaseHelper;
    private TextView textViewAlreadyAccount;

    private String NAME_OF_OWNER = "VONHATHUY";
    private String NAME_OF_RESTAURANT = "VONHATHUY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_signup);

        // Ánh xạ các view với các biến
        editTextNameOwner = findViewById(R.id.editTextNameOwner);
        editTextNameRestaurant = findViewById(R.id.editTextNameRestaurant);
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        btnSignup = findViewById(R.id.buttonSignUp);
        textViewAlreadyAccount = findViewById(R.id.textViewAlreadyAccount);

        databaseHelper = new UserDatabaseHelper(this);

        // Đặt OnClickListener cho nút Đăng ký
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        textViewAlreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivitySignup.this, AdminActivityLogin.class);
                startActivity(intent);
            }
        });
    }

    private void signUpUser() {
        String nameOwner = editTextNameOwner.getText().toString().trim();
        String nameRestaurant = editTextNameRestaurant.getText().toString().trim();
        String username = editTextName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Kiểm tra xem các trường có trống không
        if (nameOwner.isEmpty() || nameRestaurant.isEmpty() || password.isEmpty() || username.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra xem nameOwner và nameRestaurant có trùng với giá trị định sẵn không
        if (!nameOwner.equals(NAME_OF_OWNER)) {
            Toast.makeText(this, "Nhập sai tên của Owner", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!nameRestaurant.equals(NAME_OF_RESTAURANT)) {
            Toast.makeText(this, "Nhập sai tên của nhà hàng", Toast.LENGTH_SHORT).show();
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

        // Mở cơ sở dữ liệu để kiểm tra và thêm người dùng
        databaseHelper.open();

        // Kiểm tra nếu tên người dùng đã tồn tại trong cơ sở dữ liệu
        if (databaseHelper.checkIfUsernameExists(username)) {
            Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            databaseHelper.close();
            return;
        }

        if (databaseHelper.isPhoneExist(phone)) {
            Toast.makeText(this, "Phone number already exists", Toast.LENGTH_SHORT).show();
            databaseHelper.close();
            return;
        }

        // Thêm người dùng vào cơ sở dữ liệu với isAdmin = true
        User newAdmin = new User(0, username, password, username, phone, "", "", "https://example.com/default-avatar.png", true);
        long result = databaseHelper.addUser(newAdmin);
        databaseHelper.close();

        // Kiểm tra xem người dùng có được thêm thành công không
        if (result != -1) {
            Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
            // Chuyển hướng tới trang đăng nhập của admin
            Intent loginIntent = new Intent(AdminActivitySignup.this, AdminActivityLogin.class);
            startActivity(loginIntent);
            finish(); // Đóng Activity hiện tại
        } else {
            Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
        }
    }
}
