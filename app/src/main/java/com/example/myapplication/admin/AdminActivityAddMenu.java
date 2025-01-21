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

        // Initialize database
        databaseHelper = new ProductDatabaseHelper(this);
        databaseHelper.open();

        // Initialize ImageHelper
        imageHelper = new ImageHelper(this, findViewById(R.id.imgGallery));

        // Load categories from database and set in dropdown
        loadCategoriesFromDatabase();

        // Check if there's data passed via Intent to determine if it's an update
        checkForUpdateData();

        // Handle image selection
        btnGallery.setOnClickListener(v -> imageHelper.openGallery());

        // Handle product upload or update
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
        imgGallery = findViewById(R.id.imgGallery);
    }

    private void loadCategoriesFromDatabase() {
        // Open database connection
        CategoryDatabaseHelper dbHelper = new CategoryDatabaseHelper(this);
        dbHelper.open();

        // Get list of category names from database
        ArrayList<String> categoryNames = dbHelper.getAllCategoryIds();

        // Close database connection after fetching data
        dbHelper.close();

        // Assign category list to adapter
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, categoryNames);

        // Set adapter to MaterialAutoCompleteTextView
        productCategoryId.setAdapter(categoryAdapter);

        // Prevent free-text input
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

        if (databaseHelper.isProductCodeExists(code)) {
            showToast("Mã sản phẩm đã tồn tại. Vui lòng sử dụng mã khác.");
            return;
        }

        double price = Double.parseDouble(priceText);
        setButtonsEnabled(false);
        showLoadingToast("Uploading product...");

        imageHelper.saveImageToLocalFolder(imageHelper.getImageUri(), "ProductImages", new ImageHelper.ImageSaveCallback() {
            @Override
            public void onImageSaved(String filePath) {
                ProductModel product = new ProductModel(code, name, price, description, filePath, category);
                long result = databaseHelper.addProduct(product);

                runOnUiThread(() -> {
                    if (loadingToast != null) loadingToast.cancel();
                    showToast(result > 0 ? "Thêm sản phẩm vào Menu thành công!" : "Thêm sản phẩm vào Menu thất bại!");
                    setButtonsEnabled(true);
                });
            }

            @Override
            public void onError() {
                runOnUiThread(() -> {
                    if (loadingToast != null) loadingToast.cancel();
                    showToast("Lưu ảnh thất bại!");
                    setButtonsEnabled(true);
                });
            }
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
        showLoadingToast("Cập nhật sản phẩm...");

        if (imageHelper.hasImageChanged()) {
            imageHelper.saveImageToLocalFolder(imageHelper.getImageUri(), "ProductImages", new ImageHelper.ImageSaveCallback() {
                @Override
                public void onImageSaved(String filePath) {
                    ProductModel product = new ProductModel(code, name, price, description, filePath, category);
                    boolean result = databaseHelper.updateProductById(productId, product);

                    runOnUiThread(() -> {
                        if (loadingToast != null) loadingToast.cancel();
                        showToast(result ? "Cập nhật sản phẩm thành công!" : "Cập nhật sản phẩm thất bại!");
                        backScreen();
                        setButtonsEnabled(true);
                    });
                }

                @Override
                public void onError() {
                    runOnUiThread(() -> {
                        if (loadingToast != null) loadingToast.cancel();
                        showToast("Lưu ảnh thất bại!");
                        setButtonsEnabled(true);
                    });
                }
            });
        } else {
            ProductModel product = new ProductModel(code, name, price, description, originalImageUrl, category);
            boolean result = databaseHelper.updateProductById(productId, product);

            runOnUiThread(() -> {
                if (loadingToast != null) loadingToast.cancel();
                showToast(result ? "Cập nhật sản phẩm thành công!" : "Cập nhật sản phẩm thất bại!");
                backScreen();
                setButtonsEnabled(true);
            });
        }
    }

    private boolean validateInputs(String code, String name, String priceText, String description, String category) {
        if (code.isEmpty() || name.isEmpty() || priceText.isEmpty() || description.isEmpty() || category.isEmpty()) {
            showToast("Vui lòng điền đầy đủ thông tin!");
            return false;
        }
        try {
            Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            showToast("Giá trị giá không hợp lệ!");
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
