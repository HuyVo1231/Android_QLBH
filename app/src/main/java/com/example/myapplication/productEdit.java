package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.Product;

public class productEdit extends AppCompatActivity {

    private static final String TAG = "ProductEditActivity";
    private ProductDatabaseHelper productDbHelper;
    private int productId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);

        Log.d(TAG, "Activity created");

        // Initialize the database helper
        productDbHelper = new ProductDatabaseHelper(this);

        // Bind views
        EditText nameProductEditText = findViewById(R.id.nameproduct);
        EditText priceProductEditText = findViewById(R.id.priceproduct);
        Button saveButton = findViewById(R.id.save);
        Button deleteButton = findViewById(R.id.deleteBtn);

        // Get data from Intent
        if (getIntent() != null) {
            productId = getIntent().getIntExtra("product_id", -1);
            String productName = getIntent().getStringExtra("product_name");
            double productPrice = getIntent().getDoubleExtra("product_price", 0.0);

            Log.d(TAG, "Received Intent Data: productId=" + productId + ", productName=" + productName + ", productPrice=" + productPrice);

            // Nếu có dữ liệu, hiển thị lên giao diện
            if (productId != -1) {
                nameProductEditText.setText(productName);
                priceProductEditText.setText(String.valueOf(productPrice));
            }
        }

        // Handle the "Save" button click to add or edit a product
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Save button clicked");
                String name = nameProductEditText.getText().toString().trim();
                String priceStr = priceProductEditText.getText().toString().trim();

                if (name.isEmpty() || priceStr.isEmpty()) {
                    Log.w(TAG, "Validation failed: Empty fields");
                    Toast.makeText(productEdit.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price = Double.parseDouble(priceStr);

                // Open the database
                Log.d(TAG, "Opening database");
                productDbHelper.open();

                if (productId == -1) {
                    // Nếu không có ID, thêm sản phẩm mới
                    Product newProduct = new Product(0, name, price);

                    long result = productDbHelper.addProduct2(newProduct);
                    if (result != -1) {
                        Log.d(TAG, "Product added successfully with ID: " + result);
                        Toast.makeText(productEdit.this, "Product added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(productEdit.this, "Error adding product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Nếu có ID, cập nhật sản phẩm
                    Product updatedProduct = new Product(productId, name, price);

                    boolean result = productDbHelper.updateProduct(updatedProduct);
                    if (result) {
                        Log.d(TAG, "Product updated successfully for ID: " + productId);
                        Toast.makeText(productEdit.this, "Product updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(productEdit.this, "Error updating product", Toast.LENGTH_SHORT).show();
                    }
                }
                productDbHelper.close();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();

            }
        });

        // Handle the "Delete" button click to clear fields (optional)
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Delete button clicked");
                if (productId != -1) {
                    Log.d(TAG, "Attempting to delete product with ID: " + productId);

                    productDbHelper.open();
                    boolean result = productDbHelper.deleteProduct(productId);
                    productDbHelper.close();

                    if (result) {
                        Log.d(TAG, "Product deleted successfully with ID: " + productId);
                        Toast.makeText(productEdit.this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Log.e(TAG, "Error deleting product with ID: " + productId);
                        Toast.makeText(productEdit.this, "Error deleting product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "Clearing product details");
                    nameProductEditText.setText("");
                    priceProductEditText.setText("");
                    Toast.makeText(productEdit.this, "Product details cleared", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
