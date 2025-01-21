package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonLogin;
    EditText editTextPhone, editTextPassword;
    CheckBox checkBoxRemember;
    TextView toRegister;
    UserDatabaseHelper dbHelper;
    SharedPreferncesClass sharedPreferncesClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        dbHelper = new UserDatabaseHelper(this);
        sharedPreferncesClass = new SharedPreferncesClass(this);

        initializeViews();

        // Khi checkbox được check, tự động điền thông tin đăng nhập
        loadSavedLoginDetails();

        buttonLogin.setOnClickListener(this);
        toRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                checkLogin();
                break;
            case R.id.toRegister:
                ToRegister();
                break;
        }
    }

    private void initializeViews() {
        buttonLogin = findViewById(R.id.buttonLogin);
        toRegister = findViewById(R.id.toRegister);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);
    }

    public void ToRegister() {
        Intent registerIntent = new Intent(this, SignupActivity.class);
        startActivity(registerIntent);
    }

    // Method to check login credentials
    public void checkLogin() {
        String phone = editTextPhone.getText().toString();
        String password = editTextPassword.getText().toString();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.open();
        User user = dbHelper.login(phone, password);

        // Login thành công.
        if (user != null) {
            // Đưa info vào UserManager.
            UserManager.getInstance().setUser(user);

            // Lưu thông tin vào SharedPreferences nếu checkbox được check
            if (checkBoxRemember.isChecked()) {
                sharedPreferncesClass.saveLoginDetails(phone, password);
            } else {
                // Nếu không check, vẫn lưu lại nhưng xóa thông tin khi logout hoặc lần sau login
                sharedPreferncesClass.clearLoginDetails();
            }

            // Chuyển màn hình.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Failed login
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
        }

        dbHelper.close();
    }

    // Hàm để tự động điền thông tin đăng nhập khi Remember Me được check
    private void loadSavedLoginDetails() {
        String savedUserName = sharedPreferncesClass.getUserName();
        String savedPassword = sharedPreferncesClass.getPassword();

        if (!savedUserName.isEmpty() && !savedPassword.isEmpty()) {
            editTextPhone.setText(savedUserName);
            editTextPassword.setText(savedPassword);
            checkBoxRemember.setChecked(true);
        }
    }
}
