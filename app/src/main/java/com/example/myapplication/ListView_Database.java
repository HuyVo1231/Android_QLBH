package com.example.myapplication;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.adapter.AdapterProduct;
import com.example.myapplication.database.ConnectDatabase;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.Product;

import java.util.ArrayList;

public class ListView_Database extends AppCompatActivity {

    private ConnectDatabase dbHelper;
    private ProductDatabaseHelper productdb;
    private AdapterProduct adapter;
    private ArrayList<Product> products;
    private ListView listView;

    // ActivityResultLauncher for adding/editing product
    private final ActivityResultLauncher<Intent> productActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadProducts();
                    Log.d("Load Data", "Data reloaded after activity result");
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_render);

        dbHelper = new ConnectDatabase(this);
        productdb = new ProductDatabaseHelper(this);

        TextView infoTextView = findViewById(R.id.infomation);
        listView = findViewById(R.id.listproduct);
        Button createTableButton = findViewById(R.id.createtable);
        Button deleteTableButton = findViewById(R.id.deletable);
        Button addButton = findViewById(R.id.addbutton);

        createTableButton.setOnClickListener(v -> createTable());
        deleteTableButton.setOnClickListener(v -> deleteTable());

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(ListView_Database.this, productEdit.class);
            productActivityLauncher.launch(intent);
        });

        loadProducts();
    }

    private void createTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS product (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "price REAL);";
            db.execSQL(createTableSQL);
            Toast.makeText(this, "Bảng được tạo thành công", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tạo bảng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteTable() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String deleteTableSQL = "DROP TABLE IF EXISTS product;";
            db.execSQL(deleteTableSQL);
            Toast.makeText(this, "Bảng đã được xóa", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi xóa bảng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadProducts() {
        productdb.open();
        products = productdb.getAllProducts();
        productdb.close();

        if (adapter == null) {
            adapter = new AdapterProduct(this, products, productActivityLauncher);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(products);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}