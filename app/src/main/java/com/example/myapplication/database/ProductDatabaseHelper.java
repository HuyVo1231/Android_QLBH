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

    public long addProduct2(Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("price", product.getPrice());

        // Chèn dữ liệu vào bảng "product"
        long rowId = database.insert("product", null, values);

        if (rowId == -1) {
            Log.e("Database", "Thêm sản phẩm thất bại");
        } else {
            // Log thành công khi thêm sản phẩm
            Log.d("Database", "Sản phẩm đã được thêm với rowId: " + rowId);
        }

        return rowId;
    }

    public boolean updateProduct(Product product) {
        ContentValues values = new ContentValues();
        values.put("name", product.getName());
        values.put("price", product.getPrice());

        int rowsAffected = database.update("product", values, "id = ?", new String[]{String.valueOf(product.getId())});
        return rowsAffected > 0;
    }

    public boolean deleteProduct(int productId) {
        int rowsDeleted = database.delete("product", "id = ?", new String[]{String.valueOf(productId)});
        return rowsDeleted > 0;
    }

    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        Cursor cursor = database.query("product", new String[]{"id", "name", "price"},
                null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int idIndex = cursor.getColumnIndex("id");
                int nameIndex = cursor.getColumnIndex("name");
                int priceIndex = cursor.getColumnIndex("price");

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    double price = cursor.getDouble(priceIndex);

                    // Chuyển đổi id từ long sang int
                    int idInt = (int) id;

                    Product product = new Product(idInt, name, price);
                    products.add(product);
                } else {
                    Log.e("Database", "Một hoặc nhiều cột bị thiếu trong kết quả truy vấn.");
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return products;
    }

    // Lấy danh sách tất cả sản phẩm
    public ArrayList<ProductModel> getAllProducts2() {
        ArrayList<ProductModel> productList = new ArrayList<>();
        Cursor cursor = database.query("product2",
                null, null, null, null, null, null);

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

                ProductModel product = new ProductModel(id, productCode, name, price, description, imageUrl, categoryId, soldQuantity,0);
                productList.add(product);
            } while (cursor.moveToNext());
            cursor.close();
        }
        return productList;
    }

    // Lấy thông tin sản phẩm theo id
    public ProductModel getProductById(int productId) {
        ProductModel product = null;
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(productId)};

        // Truy vấn cơ sở dữ liệu
        Cursor cursor = database.query("product2",
                null, // Lấy tất cả các cột
                selection,
                selectionArgs,
                null,
                null,
                null);

        if (cursor != null && cursor.moveToFirst()) {
            // Lấy thông tin sản phẩm từ cursor
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String productCode = cursor.getString(cursor.getColumnIndexOrThrow("product_code"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
            String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));
            String categoryId = cursor.getString(cursor.getColumnIndexOrThrow("category_id"));
            int soldQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("sold_quantity"));

            // Tạo đối tượng ProductModel
            product = new ProductModel(id, productCode, name, price, description, imageUrl, categoryId, soldQuantity, 0);

            cursor.close();
        }

        return product;
    }

    // Delete product by id.
    public boolean deleteProductById(int productId) {
        int rowsDeleted = database.delete("product2", "id = ?", new String[]{String.valueOf(productId)});
        if (rowsDeleted > 0) {
            Log.d("Database", "Sản phẩm đã được xóa với id: " + productId);
        } else {
            Log.e("Database", "Xóa sản phẩm thất bại với id: " + productId);
        }
        return rowsDeleted > 0;
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
