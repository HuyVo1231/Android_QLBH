package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.example.myapplication.vnpay.VNPayURLBuilder;
import com.example.myapplication.zalopay.Api.CreateOrder;

import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

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
    private EditText textNote;
    private WebView webView;

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
        webView = findViewById(R.id.webView);

        // init zalopay
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);


        // Configure WebView for VNPay integration
        setupWebView();
    }

    private void setupWebView() {
        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE); // Disable caching to avoid stale content

        // Set up WebViewClient to handle specific URL redirections
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Handle the result of the payment if the URL contains "return_url"
                if (url.contains("return_url")) {
                    handlePaymentResult(url);
                    return true; // Prevent further handling of the URL
                }

                // Allow WebView to load other URLs normally
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Optional: Handle actions when the page has fully loaded
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                // Optional: Show error message to the user if the page fails to load
                Toast.makeText(getApplicationContext(), "Error loading page", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loadTotalAmount() {
        TextView totalAmountTextView = findViewById(R.id.textTotalAmount);
        totalAmountTextView.setText("Tổng tiền: $" + calculateAmount());
    }


    private void setupRecyclerViews() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCheckout.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupListeners() {
        btnConfirm.setOnClickListener(v -> {
            try {
                confirmOrder();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

    private void confirmOrder() throws Exception {
        if (selectedAddressPosition == -1) {
            showToast("Vui lòng chọn địa chỉ giao hàng");
        } else if (selectedPaymentMethod == -1) {
            showToast("Vui lòng chọn phương thức thanh toán");
        } else {
            if(selectedPaymentMethod == 0) {
                showToast("Thanh toan bang tien mat");
                createOrder();
            }
            if(selectedPaymentMethod == 1) {
                showToast("Thanh toan bang zalopay");
                zalopayPayment();
            }
            if(selectedPaymentMethod==2) {
                showToast("thanh toan bang vnpay");
                vnpayPayment();
            }
        }
    }


    private void addNewAddress() {
        Intent intent = new Intent(this, AddressActivity.class);
        startActivityForResult(intent, CHANGE_INFO_REQUEST_CODE);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    private void createOrder() {
        // Retrieve the selected address
        Address selectedAddress = addressAdapter.getSelectedAddress();

        // Calculate total amount
        List<ProductModel> cartProducts = CartManager.getInstance().getCartProducts();
        double totalAmount = calculateAmount();

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
        gotoScreenOrderSuccess();

    }

    // Thanh toán bằng Zalopay nè.
    private void zalopayPayment() {
        CreateOrder orderApi = new CreateOrder();

        try {
            JSONObject data = orderApi.createOrder(""+calculateAmount());
            String code = data.getString("return_code");
            Log.d("ZaloPay", " amount: " + calculateAmount());

            Log.d("ZaloPay", "Response Data: " + data.toString());

            if (code.equals("1")) {
                String token = data.getString("zp_trans_token");
                ZaloPaySDK.getInstance().payOrder(CheckoutActivity.this, token, "demozpdk://app", new PayOrderListener() {
                    @Override
                    public void onPaymentSucceeded(String s, String s1, String s2) {
                            createOrder();
                    }

                    @Override
                    public void onPaymentCanceled(String s, String s1) {
                        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                        intent.putExtra("result", "Thanh toán thất bại...");
                        startActivity(intent);
                    }

                    @Override
                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
                        Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                        intent.putExtra("result", "Thanh toán bị lỗi...");
                        startActivity(intent);
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // VNPAY
    private void vnpayPayment() throws Exception {
        // Calculate the total amount for the payment
        String amount = String.valueOf(calculateAmount());

        // Construct the VNPAY URL for the payment
        String vnpUrl = VNPayURLBuilder.generateVNPayURL(1445000 * 100);
        // Show WebView and load the payment URL
        webView.setVisibility(View.VISIBLE);
        Log.d("VNPay", "Generated VNPay URL: " + vnpUrl);
        webView.loadUrl(vnpUrl);
    }

    // Handle the result of the VNPAY payment
    private void handlePaymentResult(String url) {
        // Hide WebView after processing the payment result
        webView.setVisibility(View.GONE);

        // Check if the payment was successful based on the URL response
        if (url.contains("vnp_ResponseCode=00")) {
            // Payment was successful
            Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            // Proceed to order success screen
            gotoScreenOrderSuccess();
        } else {
            // Payment failed
            Toast.makeText(this, "Thanh toán thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to generate a unique transaction reference
    private String generateTransactionRef() {
        return UUID.randomUUID().toString();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_INFO_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Reload user data after returning from AddressActivity
            loadAddressData();
        }
    }

    // Sau khi thanh toán thành công của Zalopay.
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }

    // Tính tổng số tiền các món hàng.
    private int calculateAmount() {
        List<ProductModel> cartProducts = CartManager.getInstance().getCartProducts();
        double totalAmount = 0;

        for (ProductModel product : cartProducts) {
            totalAmount += product.getPrice() * product.getQuantity();
        }
        return (int) totalAmount;
    }

    private void backScreen() {
        finish();
    }

    private void gotoScreenOrderSuccess() {
        // Clear the cart and navigate to order success activity
        CartManager.getInstance().clearCart();

        Intent intent = new Intent(this, OrderSuccessActivity.class);
        intent.putExtra("result", "Thanh toán thành công");
        startActivity(intent);
    }
}
