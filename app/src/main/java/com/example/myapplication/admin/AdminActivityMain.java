package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;
import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

public class AdminActivityMain extends AppCompatActivity {
    CardView addMenu, addCategory, listProduct,listCategory, listOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);

        addMenu = findViewById(R.id.addMenu);
        addCategory = findViewById(R.id.addCategory);
        listProduct = findViewById(R.id.listProduct);
        listCategory = findViewById(R.id.listCategory);
        listOrder = findViewById(R.id.listOrder);

        ConnectDatabase dbHelper = new ConnectDatabase(this);
        dbHelper.getWritableDatabase();

        // Click to Add Menu Page.
        addMenu.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityMain.this, AdminActivityAddMenu.class);
            startActivity(intent);
        });

        // Click to Add Category Page.
        addCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityMain.this, AdminActivityAddCategory.class);
            startActivity(intent);
        });

        listProduct.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityMain.this, AdminAcitivityListProduct.class);
            startActivity(intent);
        });

        listCategory.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityMain.this, AdminActivityListCategory.class);
            startActivity(intent);
        });

        listOrder.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivityMain.this, AdminActivityListOrder.class);
            startActivity(intent);
        });
    }
}
