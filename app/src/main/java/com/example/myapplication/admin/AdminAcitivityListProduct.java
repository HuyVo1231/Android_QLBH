package com.example.myapplication.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.adminAdapter.AdminProductAdapter;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.ProductModel;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AdminAcitivityListProduct extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ProductModel> productList;
    private ProductDatabaseHelper databaseHelper;
    private ImageView btnBack;
    private AdminProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_listproduct);

        // Initialize database
        databaseHelper = new ProductDatabaseHelper(this);
        databaseHelper.open();

        // ListView setup
        listView = findViewById(R.id.listViewProducts);
        btnBack = findViewById(R.id.btnArrowBack);

        // Initialize product list with id
        productList = databaseHelper.getAllProducts2();
        databaseHelper.close();

        // back screen
        btnBack.setOnClickListener(v -> backScreen());

        // Set custom adapter
        adapter = new AdminProductAdapter(this, R.layout.admin_cart_item, productList);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            databaseHelper.open();
            productList.clear();
            productList.addAll(databaseHelper.getAllProducts2());
            databaseHelper.close();
            adapter.notifyDataSetChanged();
        }
    }

    private void backScreen() {
        finish();
    }
}
