package com.example.myapplication.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.OrderAdapter;
import com.example.myapplication.database.OrderDatabaseHelper;
import com.example.myapplication.model.Order;
import java.util.List;

public class AdminActivityListOrder extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private OrderDatabaseHelper databaseHelper;
    private static final int REQUEST_CODE_ORDER_DETAILS = 1;
    private ImageView btnArrowBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_listorder);

        // Initialize database helper and open connection
        databaseHelper = new OrderDatabaseHelper(this);
        databaseHelper.open();

        btnArrowBack = findViewById(R.id.btnArrowBack);
        btnArrowBack.setOnClickListener(v -> backScreen());

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadOrders();
    }

    private void loadOrders() {
        List<Order> orders = databaseHelper.getAllOrders();
        // Set up the adapter
        orderAdapter = new OrderAdapter(this, orders);
        recyclerView.setAdapter(orderAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ORDER_DETAILS && resultCode == Activity.RESULT_OK) {
            loadOrders();
        }
    }

    private void backScreen() {
        finish();
    }

}
