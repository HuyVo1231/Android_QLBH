package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.myapplication.model.CategoryModel;

import java.util.ArrayList;

public class CategoryDatabaseHelper {
    private SQLiteDatabase database;
    private final ConnectDatabase dbHelper;

    public CategoryDatabaseHelper(Context context) {
        dbHelper = new ConnectDatabase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addCategory(CategoryModel category) {
        ContentValues values = new ContentValues();
        values.put("id_category", category.getIdCategory());
        values.put("name", category.getName());
        values.put("image_url", category.getImageUrl());
        return database.insert("category", null, values);
    }

    public ArrayList<CategoryModel> getAllCategories() {
        ArrayList<CategoryModel> categoryList = new ArrayList<>();
        Cursor cursor = null;

        try {
            // Truy vấn tất cả các danh mục từ bảng category
            cursor = database.query("category",
                    new String[]{"id_category", "name", "image_url"},
                    null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Duyệt qua tất cả các bản ghi trong cursor
                do {
                    // Lấy dữ liệu từ cursor
                    String id = cursor.getString(cursor.getColumnIndexOrThrow("id_category"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("image_url"));

                    // Tạo đối tượng CategoryModel
                    CategoryModel category = new CategoryModel(id, name, imageUrl);

                    // Thêm đối tượng vào danh sách
                    categoryList.add(category);
                } while (cursor.moveToNext());  // Duyệt tiếp các bản ghi
            } else {
                Log.d("CategoryDatabaseHelper", "No categories found");
            }
        } catch (SQLException e) {
            Log.e("CategoryDatabaseHelper", "Error retrieving categories: ", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return categoryList;
    }

    // Hàm lấy tất cả id_category từ bảng category.
    public ArrayList<String> getAllCategoryIds() {
        ArrayList<String> categoryIds = new ArrayList<>();
        Cursor cursor = database.query("category", new String[]{"id_category"},
                null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                int categoryIdIndex = cursor.getColumnIndex("id_category");

                if (categoryIdIndex != -1) {
                    String categoryId = cursor.getString(categoryIdIndex);
                    categoryIds.add(categoryId);

                } else {
                    Log.e("Database", "Cột id_category bị thiếu trong bảng product.");
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        return categoryIds;
    }

    // Check category exists
    public boolean isCategoryCodeExists(String productCode) {
        String[] columns = {"id_category"};
        String selection = "id_category = ?";
        String[] selectionArgs = {productCode};
        Cursor cursor = database.query("category", columns, selection, selectionArgs, null, null, null);

        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    // Delete category by id
    public boolean deleteCategoryById(String categoryId) {
        int rowsDeleted = database.delete("category", "id_category = ?", new String[]{categoryId});
        if (rowsDeleted > 0) {
            Log.d("Database", "Danh mục đã được xóa với id: " + categoryId);
        } else {
            Log.e("Database", "Xóa danh mục thất bại với id: " + categoryId);
        }
        return rowsDeleted > 0;
    }


    // update by id
    public boolean updateCategoryById(String categoryId, CategoryModel category) {
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("image_url", category.getImageUrl());

        int rowsAffected = database.update("category", values, "id_category = ?", new String[]{categoryId});
        if (rowsAffected > 0) {
            Log.d("Database", "Danh mục đã được cập nhật với id: " + categoryId);
        } else {
            Log.e("Database", "Cập nhật danh mục thất bại với id: " + categoryId);
        }
        return rowsAffected > 0;
    }

}
