package com.example.myapplication.admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.myapplication.R;
import com.example.myapplication.database.OrderDatabaseHelper;

public class AdminActivityMain extends AppCompatActivity {
    CardView addMenu, addCategory, listProduct, listCategory, listOrder, btnStatistics, btnLogOut;
    TextView pendingOrder, completedOrder, earningMoney;
    private OrderDatabaseHelper orderDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main);

        initializeViews();
        setListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateOrderStatistics();
    }

    private void initializeViews() {
        addMenu = findViewById(R.id.addMenu);
        addCategory = findViewById(R.id.addCategory);
        listProduct = findViewById(R.id.listProduct);
        listCategory = findViewById(R.id.listCategory);
        listOrder = findViewById(R.id.listOrder);
        btnLogOut = findViewById(R.id.logOut);
        btnStatistics = findViewById(R.id.btnStatistics);

        pendingOrder = findViewById(R.id.pendingOrder);
        completedOrder = findViewById(R.id.completed_Order);
        earningMoney = findViewById(R.id.earning_Money);

        orderDbHelper = new OrderDatabaseHelper(this);
    }

    private void setListeners() {
        addMenu.setOnClickListener(v -> startActivity(new Intent(this, AdminActivityAddMenu.class)));
        addCategory.setOnClickListener(v -> startActivity(new Intent(this, AdminActivityAddCategory.class)));
        listProduct.setOnClickListener(v -> startActivity(new Intent(this, AdminAcitivityListProduct.class)));
        listCategory.setOnClickListener(v -> startActivity(new Intent(this, AdminActivityListCategory.class)));
        listOrder.setOnClickListener(v -> startActivity(new Intent(this, AdminActivityListOrder.class)));
        btnStatistics.setOnClickListener(v -> startActivity(new Intent(this, AdminActivityStatistics.class)));
        btnLogOut.setOnClickListener(v -> startActivity(new Intent(this, AdminActivityLogin.class)));
    }

    private void updateOrderStatistics() {
        orderDbHelper.open();
        int[] stats = orderDbHelper.getOrderStatistics();
        orderDbHelper.close();

        pendingOrder.setText(String.valueOf(stats[0]));
        completedOrder.setText(String.valueOf(stats[1]));
        earningMoney.setText(String.valueOf(stats[2]));
    }
}
