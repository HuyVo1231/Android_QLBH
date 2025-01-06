package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.cloudinary.CloudinaryConnect;
import com.example.myapplication.cloudinary.ImageHelper;
import com.example.myapplication.database.CategoryDatabaseHelper;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.ProductModel;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class AdminActivityAddMenu extends AppCompatActivity {
    private ImageHelper imageHelper;
    private ProductDatabaseHelper databaseHelper;
    private Toast loadingToast;
    private TextInputEditText productCode, productName, productPrice, productDescription;
    private MaterialAutoCompleteTextView productCategoryId;
    private Button btnGallery, btnUpload;
    private ImageView btnArrowback, imgGallery;
    private Integer productId = null;
    private String originalImageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_addmenu);

        // Initialize views
        initViews();

        // Initialize database and ImageHelper
        databaseHelper = new ProductDatabaseHelper(this);
        databaseHelper.open();

        // Connect cloundinary
        CloudinaryConnect.initCloudinaryConfig(this);

        imageHelper = new ImageHelper(this, findViewById(R.id.imgGallery));

        // Load categories from database and set in dropdown
        loadCategoriesFromDatabase();

        // Check if there's data passed via Intent to determine if it's an update
        checkForUpdateData();

        // Handle image selection
        btnGallery.setOnClickListener(v -> imageHelper.openGallery());

        // Handle product upload
        btnUpload.setOnClickListener(v -> {
            if (productId != null) {
                updateProduct();
            } else {
                uploadProduct();
            }
        });

        btnArrowback.setOnClickListener(v -> finish());
    }

    private void initViews() {
        productCode = findViewById(R.id.productCode);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productDescription = findViewById(R.id.productDescription);
        productCategoryId = findViewById(R.id.productCategoryId);
        btnGallery = findViewById(R.id.btnGallery);
        btnUpload = findViewById(R.id.btnUpload);
        btnArrowback = findViewById(R.id.arrowBack);
    }

    private void loadCategoriesFromDatabase() {
        // Mở kết nối với database
        CategoryDatabaseHelper dbHelper = new CategoryDatabaseHelper(this);
        dbHelper.open();

        // Lấy danh sách tên danh mục từ database
        ArrayList<String> categoryNames = dbHelper.getAllCategoryIds();

        // Đóng kết nối database sau khi lấy dữ liệu
        dbHelper.close();

        // Gán danh sách danh mục vào adapter
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryNames);

        // Gán adapter vào MaterialAutoCompleteTextView
        productCategoryId.setAdapter(categoryAdapter);

        // Ngăn không cho nhập liệu tự do
        productCategoryId.setInputType(InputType.TYPE_NULL);
        productCategoryId.setFocusable(false);
    }

    private void checkForUpdateData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("product_id")) {
            productId = intent.getIntExtra("product_id", -1);

            String code = intent.getStringExtra("product_code");
            String name = intent.getStringExtra("name");
            double price = intent.getDoubleExtra("price", 0.0);
            String description = intent.getStringExtra("description");
            String imageUrl = intent.getStringExtra("image_url");
            String categoryId = intent.getStringExtra("category_id");
            imgGallery = findViewById(R.id.imgGallery);
            originalImageUrl = imageUrl;

            productCode.setText(code);
            productName.setText(name);
            productPrice.setText(String.valueOf(price));
            productDescription.setText(description);
            productCategoryId.setText(categoryId, false);
            Glide.with(this).load(imageUrl).into(imgGallery);
        }
    }

    private void uploadProduct() {
        String code = productCode.getText().toString().trim();
        String name = productName.getText().toString().trim();
        String priceText = productPrice.getText().toString().trim();
        String description = productDescription.getText().toString().trim();
        String category = productCategoryId.getText().toString().trim();

        if (!validateInputs(code, name, priceText, description, category)) return;

        // Check if product code exists
        if (databaseHelper.isProductCodeExists(code)) {
            showToast("Mã sản phẩm đã tồn tại. Vui lòng sử dụng mã khác.");
            return;
        }

        double price = Double.parseDouble(priceText);

        setButtonsEnabled(false);
        showLoadingToast("Uploading image...");

        imageHelper.uploadImage(imageUrl -> {
            ProductModel product = new ProductModel(code, name, price, description, imageUrl, category);
            long result = databaseHelper.addProduct(product);

            runOnUiThread(() -> {
                loadingToast.cancel();
                showToast(result > 0 ? "Thêm sản phẩm vào Menu thành công!" : "Failed to add product!");

                setButtonsEnabled(true);
            });
        }, () -> {
            runOnUiThread(() -> {
                loadingToast.cancel();
                showToast("Image upload failed!");
                setButtonsEnabled(true);
            });
        });
    }

    private void updateProduct() {
        String code = productCode.getText().toString().trim();
        String name = productName.getText().toString().trim();
        String priceText = productPrice.getText().toString().trim();
        String description = productDescription.getText().toString().trim();
        String category = productCategoryId.getText().toString().trim();

        if (!validateInputs(code, name, priceText, description, category)) return;

        double price = Double.parseDouble(priceText);

        setButtonsEnabled(false);
        showLoadingToast("Updating product...");

        if (imageHelper.hasImageChanged()) { // Check if the image has changed
            imageHelper.uploadImage(imageUrl -> {
                ProductModel product = new ProductModel(code, name, price, description, imageUrl, category);
                boolean result = databaseHelper.updateProductById(productId, product);

                runOnUiThread(() -> {
                    loadingToast.cancel();
                    showToast(result ? "Cập nhật sản phẩm thành công!" : "Failed to update product!");
                    backScreen();
                    setButtonsEnabled(true);

                });
            }, () -> {
                runOnUiThread(() -> {
                    loadingToast.cancel();
                    showToast("Image upload failed!");
                    setButtonsEnabled(true);
                });
            });
        } else {
            // If the image hasn't changed, use the original image URL
            ProductModel product = new ProductModel(code, name, price, description, originalImageUrl, category);
            boolean result = databaseHelper.updateProductById(productId, product);

            runOnUiThread(() -> {
                loadingToast.cancel();
                showToast(result ? "Cập nhật sản phẩm thành công!" : "Failed to update product!");
                backScreen();
                setButtonsEnabled(true);
            });
        }
    }


    private boolean validateInputs(String code, String name, String priceText, String description, String category) {
        if (code.isEmpty() || name.isEmpty() || priceText.isEmpty() || description.isEmpty() || category.isEmpty()) {
            showToast("Please fill all fields!");
            return false;
        }
        try {
            Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showToast("Invalid price value!");
            return false;
        }
        return true;
    }

    private void setButtonsEnabled(boolean enabled) {
        productCode.setEnabled(enabled);
        productName.setEnabled(enabled);
        productPrice.setEnabled(enabled);
        productDescription.setEnabled(enabled);
        btnGallery.setEnabled(enabled);
        btnUpload.setEnabled(enabled);
    }

    private void showLoadingToast(String message) {
        if (loadingToast != null) loadingToast.cancel();
        loadingToast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        loadingToast.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private void backScreen() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}
