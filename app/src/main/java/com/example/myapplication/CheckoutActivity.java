package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.AddressAdapter;
import com.example.myapplication.adapter.CheckoutAdapter;
import com.example.myapplication.database.AddressDatabaseHelper;
import com.example.myapplication.database.OrderDatabaseHelper;
import com.example.myapplication.model.Address;
import com.example.myapplication.model.CartManager;
import com.example.myapplication.model.Order;
import com.example.myapplication.model.OrderDetail;
import com.example.myapplication.model.ProductModel;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private static final int CHANGE_INFO_REQUEST_CODE = 1;
    private RecyclerView recyclerView, recyclerViewCheckout;
    private Button btnConfirm;
    private RadioButton rbCash, rbCard, rbBankTransfer;
    private AddressAdapter addressAdapter;
    private int selectedAddressPosition = -1, selectedPaymentMethod = -1;
    private LinearLayout btnAddAddress;
    private AddressDatabaseHelper addressDatabaseHelper;
    private ImageView btnArrowBack;
    private TextView textTotalAmount;
    private EditText textNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        loadTotalAmount();
        initializeViews();
        setupRecyclerViews();
        setupListeners();
        loadAddressData();
        loadCartData();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerViewCheckout = findViewById(R.id.recyclerViewCheckout);
        btnConfirm = findViewById(R.id.btnConfirm);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        rbCash = findViewById(R.id.rbCash);
        rbCard = findViewById(R.id.rbCard);
        rbBankTransfer = findViewById(R.id.rbBankTransfer);
        btnArrowBack = findViewById(R.id.btnArrowBack);
        textNote = findViewById(R.id.textNote);

    }

    private void loadTotalAmount() {
        List<ProductModel> cartProducts = CartManager.getInstance().getCartProducts();
        double totalAmount = 0;

        for (ProductModel product : cartProducts) {
            totalAmount += product.getPrice() * product.getQuantity();
        }

        TextView totalAmountTextView = findViewById(R.id.textTotalAmount);
        totalAmountTextView.setText("Tổng tiền: $" + totalAmount);
    }


    private void setupRecyclerViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCheckout.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(v -> confirmOrder());
        btnAddAddress.setOnClickListener(v -> addNewAddress());
        btnArrowBack.setOnClickListener(v -> backScreen());

        rbCash.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedPaymentMethod = 0;
        });
        rbCard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedPaymentMethod = 1;
        });
        rbBankTransfer.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedPaymentMethod = 2;
        });
    }

    private void loadAddressData() {
        User currentUser = UserManager.getInstance().getUser();

        addressDatabaseHelper = new AddressDatabaseHelper(this);
        addressDatabaseHelper.open();
        List<Address> addressList = addressDatabaseHelper.getAllAddressesByUserId(currentUser.getId());
        addressDatabaseHelper.close();

        addressAdapter = new AddressAdapter(this, addressList, -1);
        recyclerView.setAdapter(addressAdapter);

        addressAdapter.setOnItemClickListener(new AddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                selectedAddressPosition = position;
            }
        });

    }

    private void loadCartData() {
        List<ProductModel> cartProducts = CartManager.getInstance().getCartProducts();
        CheckoutAdapter checkoutAdapter = new CheckoutAdapter(this, cartProducts);
        recyclerViewCheckout.setAdapter(checkoutAdapter);
    }

    private void confirmOrder() {
        if (selectedAddressPosition == -1) {
            showToast("Vui lòng chọn địa chỉ giao hàng");
        } else if (selectedPaymentMethod == -1) {
            showToast("Vui lòng chọn phương thức thanh toán");
        } else {
            // Retrieve the selected address
            Address selectedAddress = addressAdapter.getSelectedAddress();

            // Calculate total amount
            List<ProductModel> cartProducts = CartManager.getInstance().getCartProducts();
            double totalAmount = 0;
            for (ProductModel product : cartProducts) {
                totalAmount += product.getPrice() * product.getQuantity();
            }

            String note = textNote.getText().toString();
            // Create and add order to database
            OrderDatabaseHelper orderDatabaseHelper = new OrderDatabaseHelper(this);
            orderDatabaseHelper.open();

            User currentUser = UserManager.getInstance().getUser();
            Order order = new Order(0,currentUser.getId(), selectedAddress.getId(), totalAmount, selectedPaymentMethod,"", "Đang giao hàng",note,"");
            long orderId = orderDatabaseHelper.addOrder(order);

            // Create and add order details to database
            for (ProductModel product : cartProducts) {
                OrderDetail orderDetail = new OrderDetail(0,orderId, product.getId(), product.getQuantity(), product.getPrice(),"","");
                orderDatabaseHelper.addOrderDetail(orderDetail);
            }

            orderDatabaseHelper.close();

            // Clear the cart and navigate to order success activity
            CartManager.getInstance().clearCart();
            startActivity(new Intent(this, OrderSuccessActivity.class));
        }
    }


    private void addNewAddress() {
        Intent intent = new Intent(this, AddressActivity.class);
        startActivityForResult(intent, CHANGE_INFO_REQUEST_CODE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_INFO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Reload user data after returning from AddressActivity
            loadAddressData();
        }
    }

    private void backScreen() {
        finish();
    }

}
