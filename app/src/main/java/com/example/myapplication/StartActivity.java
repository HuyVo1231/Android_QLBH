package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.myapplication.admin.AdminActivityLogin;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonAdmin, buttonUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        renderLayout();
    }

    public void renderLayout() {
        buttonAdmin = findViewById(R.id.btnAdmin);
        buttonAdmin.setOnClickListener(this);

        buttonUser = findViewById(R.id.btnUser);
        buttonUser.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        // Kiểm tra button nào được nhấn và chuyển đến Activity tương ứng
        if (view.getId() == R.id.btnAdmin) {
            // Nếu nhấn vào Admin, chuyển đến AdminLoginActivity
            intent = new Intent(this, AdminActivityLogin.class);
        } else if (view.getId() == R.id.btnUser) {
            // Nếu nhấn vào User, chuyển đến LoginActivity
            intent = new Intent(this, LoginActivity.class);
        }

        // Chuyển đến Activity tương ứng
        if (intent != null) {
            startActivity(intent);
        }
    }
}
