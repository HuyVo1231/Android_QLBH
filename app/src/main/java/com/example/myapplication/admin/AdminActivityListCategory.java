package com.example.myapplication.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.adminAdapter.AdminCategoryAdapter; // Assume you have created this adapter for categories
import com.example.myapplication.database.CategoryDatabaseHelper;
import com.example.myapplication.model.CategoryModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AdminActivityListCategory extends AppCompatActivity {

    private ListView listView;
    private ArrayList<CategoryModel> categoryList;
    private CategoryDatabaseHelper databaseHelper;
    private ImageView btnBack;
    private AdminCategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_listcategory);

        // Initialize database
        databaseHelper = new CategoryDatabaseHelper(this);
        databaseHelper.open();

        // ListView setup
        listView = findViewById(R.id.listViewCategories);
        btnBack = findViewById(R.id.btnArrowBack);

        // Initialize category list with id
        categoryList = databaseHelper.getAllCategories();
        databaseHelper.close();

        // back screen
        btnBack.setOnClickListener(v -> backScreen());

        // Set custom adapter
        adapter = new AdminCategoryAdapter(this, R.layout.admin_category_item, categoryList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Reload your data here
            databaseHelper.open();
            categoryList.clear();
            categoryList.addAll(databaseHelper.getAllCategories());
            databaseHelper.close();
            adapter.notifyDataSetChanged();
        }
    }

    private void backScreen() {
        finish();
    }
}
