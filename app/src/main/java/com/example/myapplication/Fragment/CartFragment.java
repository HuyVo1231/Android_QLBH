package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.myapplication.CheckoutActivity;
import com.example.myapplication.R;
import com.example.myapplication.adapter.CartAdapter;
import com.example.myapplication.model.CartManager;
import com.example.myapplication.model.ProductModel;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private ListView listView;
    private TextView textTotalQuantity, textTotalAmount;
    private Button btnCheckout;

    private ArrayList<ProductModel> productList;
    private CartAdapter cartAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        // Find views
        listView = view.findViewById(R.id.listView);
        textTotalQuantity = view.findViewById(R.id.textTotalQuantity);
        textTotalAmount = view.findViewById(R.id.textTotalAmount);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        // Lấy danh sách sản phẩm từ CartManager
        productList = new ArrayList<>(CartManager.getInstance().getCartProducts());

        // Set adapter
        cartAdapter = new CartAdapter(getContext(), R.layout.cart_item, productList, this::updateSummary);
        listView.setAdapter(cartAdapter);

        // Update summary
        updateSummary();

        // Nếu trong giỏ hàng có sản phẩm, thì mới pass.
        btnCheckout.setOnClickListener(v -> {
            if (!productList.isEmpty()) {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getContext(), "Bạn chưa thêm sản phẩm nào!", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void updateSummary() {
        int totalQuantity = 0;
        double totalAmount = 0;

        for (ProductModel product : productList) {
            totalQuantity += product.getQuantity();
            totalAmount += product.getQuantity() * product.getPrice();
        }

        // Update UI
        textTotalQuantity.setText("Tổng số lượng: " + totalQuantity);
        textTotalAmount.setText(String.format("Tổng tiền: $%.2f", totalAmount));
    }
}
