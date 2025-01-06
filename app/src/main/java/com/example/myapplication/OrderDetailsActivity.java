package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.AddressAdapter;
import com.example.myapplication.adapter.OrderDetailAdapter;
import com.example.myapplication.database.AddressDatabaseHelper;
import com.example.myapplication.database.OrderDatabaseHelper;
import com.example.myapplication.model.Address;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.OrderDetail;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {

    private OrderDatabaseHelper dbHelper;
    private AddressDatabaseHelper addressDatabaseHelper;
    private RecyclerView orderDetailRecyclerView;
    private RecyclerView addressRecyclerView;
    private OrderDetailAdapter orderDetailAdapter;
    private AddressAdapter addressAdapter;
    private List<OrderDetail> orderDetailList;
    private AutoCompleteTextView orderStatusDropdown;
    private Boolean ADMIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        int orderId = getIntent().getIntExtra("order_id", -1);
        int addressId = getIntent().getIntExtra("address_id", -1);
        dbHelper = new OrderDatabaseHelper(this);
        dbHelper.open();
        addressDatabaseHelper = new AddressDatabaseHelper(this);
        addressDatabaseHelper.open();

        setupDropdownMenu();
        setupOrderDetails(orderId);
        setupOrderRecyclerView(orderId);
        setupAddressRecyclerView(addressId);

        dbHelper.close();
        addressDatabaseHelper.close();
        setupBackButton();

    }

    private void setupOrderDetails(int orderId) {
        Order order = dbHelper.getOrderById(orderId);

        TextView orderDate = findViewById(R.id.orderDate);
        TextView orderTotal = findViewById(R.id.orderTotal);

        orderDate.setText("Ngày đặt hàng: " + order.getOrderDate());
        orderTotal.setText("Tổng tiền: $" + order.getTotalAmount());

        orderStatusDropdown.setText(order.getStatus(), false);

        // Pass the selected address ID to the AddressAdapter
        int selectedAddressId = order.getAddressId();
        setupAddressRecyclerView(selectedAddressId);
    }

    private void setupOrderRecyclerView(int orderId) {
        orderDetailList = dbHelper.getOrderDetailsByOrderId(orderId);
        orderDetailRecyclerView = findViewById(R.id.purchasedProductsRecyclerView);
        orderDetailRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderDetailAdapter = new OrderDetailAdapter(this, orderDetailList);
        orderDetailRecyclerView.setAdapter(orderDetailAdapter);
    }

    private void setupAddressRecyclerView(int selectedAddressId) {
        int addressId = getIntent().getIntExtra("address_id", -1);

        List<Address> addressList = addressDatabaseHelper.getAddressById(addressId);

        addressRecyclerView = findViewById(R.id.addressRecyclerView);
        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(this, addressList, selectedAddressId);
        addressRecyclerView.setAdapter(addressAdapter);
    }

    private void setupBackButton() {
        ImageView btnArrowBack = findViewById(R.id.btnArrowBack);
        btnArrowBack.setOnClickListener(v -> backScreen());
    }

    private void backScreen() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void setupDropdownMenu() {
        // Kết nối AutoCompleteTextView
        orderStatusDropdown = findViewById(R.id.orderStatusDropdown);
        User currentUser = UserManager.getInstance().getUser();

        String[] orderStatuses;
        if (currentUser != null && currentUser.isAdmin()) {
            orderStatuses = getResources().getStringArray(R.array.order_status_array_admin);
        } else {
            orderStatuses = getResources().getStringArray(R.array.order_status_array);
        }

        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, orderStatuses);

        // Set the Adapter to AutoCompleteTextView
        orderStatusDropdown.setAdapter(adapter);

        // Handle item selection in the dropdown
        orderStatusDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedStatus = (String) parent.getItemAtPosition(position);
            Toast.makeText(this, "Bạn chọn: " + selectedStatus, Toast.LENGTH_SHORT).show();

            // Perform other actions if needed
            updateOrderStatus(selectedStatus);
        });
    }

    private void updateOrderStatus(String status) {
        int orderId = getIntent().getIntExtra("order_id", -1);

        if(orderId != -1) {
            dbHelper.open();
            dbHelper.updateOrderStatus(orderId, status);
            dbHelper.close();
            Toast.makeText(this, "Trạng thái đơn hàng đã được cập nhật: " + status, Toast.LENGTH_SHORT).show();
            backScreen();
        }
        else {
            Toast.makeText(this, "Không tìm thấy ID đơn hàng", Toast.LENGTH_SHORT).show();
        }

    }

}
