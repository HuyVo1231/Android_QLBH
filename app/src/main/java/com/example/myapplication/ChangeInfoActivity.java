package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.cloudinary.ImageHelper;
import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;
import com.google.android.material.textfield.TextInputEditText;

public class ChangeInfoActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText, fullNameEditText, emailEditText, addressEditText;
    private ImageView profileImageView, btnBack;
    private Button btnGallery, btnUpload;
    private ImageHelper imageHelper;
    private Toast loadingToast;
    private UserDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        // Initialize UI components
        initializeViews();

        databaseHelper = new UserDatabaseHelper(this);
        databaseHelper.open();

        imageHelper = new ImageHelper(this, profileImageView);

        renderInfoUser();
        setupListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private void initializeViews() {
        usernameEditText = findViewById(R.id.username);
        fullNameEditText = findViewById(R.id.fullName);
        emailEditText = findViewById(R.id.email);
        addressEditText = findViewById(R.id.address);
        profileImageView = findViewById(R.id.imgGallery);
        btnGallery = findViewById(R.id.btnGallery);
        btnUpload = findViewById(R.id.btnUpload);
        btnBack = findViewById(R.id.arrowBack);
    }

    private void setupListeners() {
        // Open gallery to select an image
        btnGallery.setOnClickListener(v -> imageHelper.openGallery());

        // Set the save button click listener
        btnUpload.setOnClickListener(v -> updateUserInfo());

        // back arrow
        btnBack.setOnClickListener(v -> backScreen());
    }

    // Update user information in the database
    private void updateUserInfo() {
        // Get information from EditText fields
        String username = usernameEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Get the current user from UserManager
        User currentUser = UserManager.getInstance().getUser();

        // Validate email format
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if image has changed
        if (imageHelper.hasImageChanged()) {
            loadingToast = Toast.makeText(this, "Đang tải ảnh...", Toast.LENGTH_LONG);
            loadingToast.show();

            // Save image locally if the user has selected a new image
            imageHelper.saveImageToLocalFolder(imageHelper.getImageUri(), "ProfileImages", new ImageHelper.ImageSaveCallback() {
                @Override
                public void onImageSaved(String filePath) {
                    // Update current user information
                    currentUser.setUsername(username);
                    currentUser.setFullName(fullName);
                    currentUser.setEmail(email);
                    currentUser.setAddress(address);
                    currentUser.setImageUrl(filePath);

                    // Update in database
                    boolean result = databaseHelper.updateUser(currentUser);

                    // Save user information to UserManager
                    UserManager.getInstance().setUser(currentUser);

                    runOnUiThread(() -> {
                        if (loadingToast != null) loadingToast.cancel();
                        if (result) {
                            Toast.makeText(ChangeInfoActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                            backScreen();
                        } else {
                            Toast.makeText(ChangeInfoActivity.this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onError() {
                    runOnUiThread(() -> {
                        if (loadingToast != null) loadingToast.cancel();
                        Toast.makeText(ChangeInfoActivity.this, "Lưu ảnh thất bại!", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // If the image hasn't changed, just update the information
            currentUser.setUsername(username);
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setAddress(address);

            // Update in database
            boolean result = databaseHelper.updateUser(currentUser);

            // Save user information to UserManager
            UserManager.getInstance().setUser(currentUser);

            // Notify the user
            if (result) {
                Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void renderInfoUser() {
        // Get the current user from UserManager
        User currentUser = UserManager.getInstance().getUser();

        if (currentUser != null) {
            // Populate the UI with the current user data
            usernameEditText.setText(currentUser.getUsername());
            usernameEditText.setEnabled(false);
            fullNameEditText.setText(currentUser.getFullName());
            emailEditText.setText(currentUser.getEmail());
            addressEditText.setText(currentUser.getAddress());

            // Load the profile image if available
            Glide.with(this).load(currentUser.getImageUrl()).into(profileImageView);
        } else {
            // Handle case where no user is logged in or available
            Log.e("ChangeInfoActivity", "User data is not available");
        }
    }

    private void backScreen() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"; // Regular expression for email
        return email.matches(emailPattern);
    }
}
