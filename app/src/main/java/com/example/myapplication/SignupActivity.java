package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;

public class SignupActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextPassword;
    private Button signUpButton;
    private TextView alreadyHaveAccount;

    private UserDatabaseHelper userDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize views
        initializeViews();

        // Initialize database helper
        userDatabaseHelper = new UserDatabaseHelper(this);

        // Set up sign-up button click listener
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpUser();
            }
        });

        // Set up already have an account click listener to navigate to login
        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    private void initializeViews() {
        editTextName = findViewById(R.id.editTextName);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        signUpButton = findViewById(R.id.button2);
        alreadyHaveAccount = findViewById(R.id.textView9);
    }

    // Method to handle sign-up
    private void signUpUser() {
        String username = editTextName.getText().toString().trim();
        String numberphone = editTextPhone.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate user input
        if (username.isEmpty() || numberphone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the username already exists in the database
        userDatabaseHelper.open();
        if (userDatabaseHelper.checkIfUsernameExists(username) || userDatabaseHelper.isPhoneExist(numberphone)) {
            Toast.makeText(this, "Username or numberphone already exists", Toast.LENGTH_SHORT).show();
            userDatabaseHelper.close();
            return;
        }

        // Phone number validation (ensure it has only digits and a valid length)
        if (!numberphone.matches("[0-9]+") || numberphone.length() != 10) {
            Toast.makeText(this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Password validation (minimum 6 characters for example)
        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user object
        User newUser = new User(0,username, password, username, numberphone, "", "","/data/data/com.example.myapplication/files/avatar-default.png",false);

        // Add user to the database
        long result = userDatabaseHelper.addUser(newUser);
        userDatabaseHelper.close();

        // Check if user was successfully added
        if (result != -1) {
            Toast.makeText(this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();
            // Navigate to login page after successful sign-up
            Intent loginIntent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish(); // Close signup activity
        } else {
            Toast.makeText(this, "Sign Up Failed", Toast.LENGTH_SHORT).show();
        }
    }

}
