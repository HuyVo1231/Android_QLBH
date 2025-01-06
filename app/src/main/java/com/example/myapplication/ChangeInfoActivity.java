package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.cloudinary.CloudinaryConnect;
import com.example.myapplication.cloudinary.ImageHelper;
import com.example.myapplication.database.CategoryDatabaseHelper;
import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.CategoryModel;
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

        // Connect to Cloudinary
        CloudinaryConnect.initCloudinaryConfig(this);

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
        // Lấy thông tin từ các EditText
        String username = usernameEditText.getText().toString().trim();
        String fullName = fullNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        // Lấy thông tin người dùng hiện tại từ UserManager
        User currentUser = UserManager.getInstance().getUser();

        // Validate email format
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Email không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra nếu ảnh có thay đổi
        if (imageHelper.hasImageChanged()) {
            loadingToast = Toast.makeText(this, "Đang tải ảnh...", Toast.LENGTH_LONG);
            loadingToast.show();

            // Upload ảnh nếu người dùng đã chọn ảnh mới
            imageHelper.uploadImage(imageUrl -> {
                // Cập nhật thông tin người dùng hiện tại
                currentUser.setUsername(username);
                currentUser.setFullName(fullName);
                currentUser.setEmail(email);
                currentUser.setAddress(address);
                currentUser.setImageUrl(imageUrl);

                // Cập nhật vào cơ sở dữ liệu
                boolean result = databaseHelper.updateUser(currentUser);

                // Lưu thông tin người dùng vào UserManager
                UserManager.getInstance().setUser(currentUser);

                runOnUiThread(() -> {
                    loadingToast.cancel();
                    if (result) {
                        Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                        backScreen();
                    } else {
                        Toast.makeText(this, "Cập nhật thông tin thất bại!", Toast.LENGTH_SHORT).show();
                    }
                });
            }, () -> {
                // Nếu tải ảnh thất bại
                runOnUiThread(() -> {
                    loadingToast.cancel();
                    Toast.makeText(this, "Tải ảnh thất bại!", Toast.LENGTH_SHORT).show();
                });
            });
        } else {
            // Nếu ảnh không thay đổi, chỉ cập nhật thông tin
            currentUser.setUsername(username);
            currentUser.setFullName(fullName);
            currentUser.setEmail(email);
            currentUser.setAddress(address);

            // Cập nhật vào cơ sở dữ liệu
            boolean result = databaseHelper.updateUser(currentUser);

            // Lưu thông tin người dùng vào UserManager
            UserManager.getInstance().setUser(currentUser);

            // Hiển thị thông báo cho người dùng
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
