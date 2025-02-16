package com.example.myapplication.database;
import android.util.Log;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.example.myapplication.model.Product;
import com.example.myapplication.model.ProductModel;

import java.util.ArrayList;

public class ProductDatabaseHelper {
    private SQLiteDatabase database;
    private ConnectDatabase dbHelper;

    public ProductDatabaseHelper(Context context) {
        dbHelper = new ConnectDatabase(context);
    }

    // Open database connection
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Close database connection
    public void close() {
        dbHelper.close();
    }

    // Add product to the "product" table
    public long addProduct(ProductModel product) {
        // Khởi tạo ContentValues để chứa dữ liệu cần chèn
        ContentValues values = new ContentValues();
        values.put("product_code", product.getProductCode());
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("description", product.getDescription());
        values.put("image_url", product.getImageUrl());
        values.put("category_id", product.getCategoryId());

        // Chèn dữ liệu vào bảng "product"
        long rowId = database.insert("product2", null, values);

        if (rowId == -1) {
            Log.e("Database", "Thêm sản phẩm thất bại");
        } else {
            // Log thành công khi thêm sản phẩm
            Log.d("Database", "Sản phẩm đã được thêm với rowId: " + rowId);
        }

        return rowId;
    }

    public boolean isProductCodeExists(String productCode) {
        String[] columns = {"product_code"};
        String selection = "product_code = ?";
        String[] selectionArgs = {productCode};
        Cursor cursor = database.query("product2", columns, selection, selectionArgs, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Lấy danh sách tất cả sản phẩm
    public ArrayList<ProductModel> getAllProducts2() {
        ArrayList<ProductModel> productList = new ArrayList<>();
        String selection = "status = ?";
        String[] selectionArgs = {"1"};

        Cursor cursor = database.query("product2",
                null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String productCode = cursor.getString(cursor.getColumnIndexOrThrow("product_code"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
                String categoryId = cursor.getString(cursor.getColumnIndexOrThrow("category_id"));
                int soldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sold_quantity"));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

                ProductModel product = new ProductModel(id, productCode, name, price, description, imageUrl, categoryId, soldQuantity, status);
                productList.add(product);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return productList;
    }

    // Lấy thông tin sản phẩm theo id
    public ProductModel getProductById(int productId) {
        ProductModel product = null;
        String selection = "id = ? AND status = ?";
        String[] selectionArgs = {String.valueOf(productId), "1"};

        Cursor cursor = database.query("product2",
                null, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String productCode = cursor.getString(cursor.getColumnIndexOrThrow("product_code"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
            String categoryId = cursor.getString(cursor.getColumnIndexOrThrow("category_id"));
            int soldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sold_quantity"));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow("status"));

            product = new ProductModel(id, productCode, name, price, description, imageUrl, categoryId, soldQuantity, status);
            cursor.close();
        }

        return product;
    }

    // Delete product by id.
    public boolean deleteProductById(int productId) {
        ContentValues values = new ContentValues();
        values.put("status", 0);

        int rowsAffected = database.update("product2", values, "id = ?", new String[]{String.valueOf(productId)});
        if (rowsAffected > 0) {
            Log.d("Database", "Sản phẩm đã được ẩn với id: " + productId);
        } else {
            Log.e("Database", "Ẩn sản phẩm thất bại với id: " + productId);
        }
        return rowsAffected > 0;
    }

    // update by id.
    public boolean updateProductById(int productId, ProductModel product) {
        ContentValues values = new ContentValues();
        values.put("product_code", product.getProductCode());
        values.put("name", product.getName());
        values.put("price", product.getPrice());
        values.put("description", product.getDescription());
        values.put("image_url", product.getImageUrl());
        values.put("category_id", product.getCategoryId());

        int rowsAffected = database.update("product2", values, "id = ?", new String[]{String.valueOf(productId)});
        if (rowsAffected > 0) {
            Log.d("Database", "Sản phẩm đã được cập nhật với id: " + productId);
        } else {
            Log.e("Database", "Cập nhật sản phẩm thất bại với id: " + productId);
        }
        return rowsAffected > 0;
    }


}
