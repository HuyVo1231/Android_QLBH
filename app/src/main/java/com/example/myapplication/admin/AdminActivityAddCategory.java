package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.cloudinary.ImageHelper;
import com.example.myapplication.database.CategoryDatabaseHelper;
import com.example.myapplication.model.CategoryModel;
import com.google.android.material.textfield.TextInputEditText;

public class AdminActivityAddCategory extends AppCompatActivity {

    private TextInputEditText idCategory, nameCategory;
    private ImageView arrowBack, imgGallery;
    private Button btnGallery, btnUpload;
    private ImageHelper imageHelper;
    private CategoryDatabaseHelper databaseHelper;
    private Toast loadingToast;
    private String categoryId = null;
    private String originalImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_addcategory);

        initViews();

        databaseHelper = new CategoryDatabaseHelper(this);
        databaseHelper.open();

        imageHelper = new ImageHelper(this, imgGallery);

        btnGallery.setOnClickListener(v -> imageHelper.openGallery());

        arrowBack.setOnClickListener(view -> onBackPressed());

        btnUpload.setOnClickListener(v -> {
            if (categoryId != null) {
                updateCategory();
            } else {
                uploadCategory();
            }
        });

        checkForUpdateData();
    }

    private void initViews() {
        idCategory = findViewById(R.id.idCategory);
        nameCategory = findViewById(R.id.nameCategory);
        arrowBack = findViewById(R.id.arrowBack);
        imgGallery = findViewById(R.id.imgGallery);
        btnGallery = findViewById(R.id.btnGallery);
        btnUpload = findViewById(R.id.btnUpload);
    }

    private void checkForUpdateData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("category_id")) {
            categoryId = intent.getStringExtra("category_id");

            String name = intent.getStringExtra("name");
            String imageUrl = intent.getStringExtra("image_url");
            originalImageUrl = imageUrl;
            idCategory.setText(categoryId);
            idCategory.setEnabled(false);
            nameCategory.setText(name);
            Glide.with(this).load(imageUrl).into(imgGallery);
        }
    }

    private void uploadCategory() {
        String categoryName = nameCategory.getText().toString().trim();
        String categoryId = idCategory.getText().toString().trim();

        if (!validateInputs(categoryId, categoryName)) return;

        if (databaseHelper.isCategoryCodeExists(categoryId)) {
            showToast("ID Category đã tồn tại");
            return;
        }

        setButtonsEnabled(false);
        showLoadingToast("Saving image...");

        imageHelper.saveImageToLocalFolder(imageHelper.getImageUri(), "CategoryImages", new ImageHelper.ImageSaveCallback() {
            @Override
            public void onImageSaved(String filePath) {
                CategoryModel category = new CategoryModel(categoryId, categoryName, filePath);
                long result = databaseHelper.addCategory(category);

                runOnUiThread(() -> {
                    if (loadingToast != null) loadingToast.cancel();
                    Toast.makeText(AdminActivityAddCategory.this, result > 0 ? "Thêm danh mục thành công!" : "Thêm thất bại!", Toast.LENGTH_SHORT).show();
                    setButtonsEnabled(true);
                    if (result > 0) setResultAndFinish();
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    if (loadingToast != null) loadingToast.cancel();
                    setButtonsEnabled(true);
                    showToast("Lỗi lưu ảnh!");
                });
            }
        });
    }

    private void updateCategory() {
        String categoryName = nameCategory.getText().toString().trim();

        if (!validateInputs(categoryId, categoryName)) return;

        setButtonsEnabled(false);
        showLoadingToast("Đang cập nhật danh mục...");

        if (imageHelper.hasImageChanged()) {
            imageHelper.saveImageToLocalFolder(imageHelper.getImageUri(), "CategoryImages", new ImageHelper.ImageSaveCallback() {
                @Override
                public void onImageSaved(String filePath) {
                    CategoryModel category = new CategoryModel(categoryId, categoryName, filePath);
                    boolean result = databaseHelper.updateCategoryById(categoryId, category);

                    runOnUiThread(() -> {
                        if (loadingToast != null) loadingToast.cancel();
                        showToast(result ? "Cập nhật danh mục thành công!" : "Cập nhật danh mục thất bại!");
                        setButtonsEnabled(true);
                        if (result) setResultAndFinish();
                    });
                }

                @Override
                public void onError() {
                    runOnUiThread(() -> {
                        if (loadingToast != null) loadingToast.cancel();
                        setButtonsEnabled(true);
                        showToast("Không thể lưu ảnh");
                    });
                }
            });
        } else {
            CategoryModel category = new CategoryModel(categoryId, categoryName, originalImageUrl);
            boolean result = databaseHelper.updateCategoryById(categoryId, category);

            runOnUiThread(() -> {
                if (loadingToast != null) loadingToast.cancel();
                showToast(result ? "Cập nhật danh mục thành công!" : "Cập nhật danh mục thất bại!");
                setButtonsEnabled(true);
                if (result) setResultAndFinish();
            });
        }
    }

    private boolean validateInputs(String idCategory, String name) {
        if (idCategory.isEmpty() || name.isEmpty()) {
            showToast("Vui lòng điền tất cả các trường!");
            return false;
        }
        return true;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void setButtonsEnabled(boolean enabled) {
        idCategory.setEnabled(enabled);
        nameCategory.setEnabled(enabled);
        btnUpload.setEnabled(enabled);
        btnGallery.setEnabled(enabled);
    }

    private void showLoadingToast(String message) {
        if (loadingToast != null) loadingToast.cancel();
        loadingToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        loadingToast.show();
    }

    private void setResultAndFinish() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHelper.handleActivityResult(requestCode, resultCode, data);
    }
}
