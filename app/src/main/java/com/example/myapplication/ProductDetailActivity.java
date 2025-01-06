package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.Fragment.CartFragment;
import com.example.myapplication.adapter.ViewProductDetailAdapter;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.CartManager;
import com.example.myapplication.model.ProductModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ImageView btnBack;
    ViewProductDetailAdapter viewProductDetailAdapter;
    ProductDatabaseHelper databaseHelper;
    ProductModel productInfo;
    ImageView productImage;
    TextView productName, productPrice;
    Button addtoCart, buynow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Ánh xạ các view
        btnBack = findViewById(R.id.backBtn);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        productImage = findViewById(R.id.imageProduct);
        productName = findViewById(R.id.nameProduct);
        productPrice = findViewById(R.id.priceProduct);
        addtoCart = findViewById(R.id.buttonAddtoCart);
        buynow = findViewById(R.id.buttonBuyNow);

        // Nhận dữ liệu từ Intent
        int id = getIntent().getIntExtra("id", 0);

        // Kết nối cơ sỡ dữ liệu, và lấy dữ liệu từ product by ID.
        databaseHelper = new ProductDatabaseHelper(this);
        databaseHelper.open();
        productInfo = databaseHelper.getProductById(id);
        databaseHelper.close();

        if (productInfo != null) {
            // Gắn dữ liệu vào các view
            Glide.with(this)
                    .load(productInfo.getImageUrl())
                    .into(productImage);
            productName.setText(productInfo.getName());
            productPrice.setText("$" + productInfo.getPrice());
        } else {
            Toast.makeText(this, "Product not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TabLayout và ViewPager2 cho các tab chi tiết
        viewProductDetailAdapter = new ViewProductDetailAdapter(this, productInfo.getDescription());
        viewPager2.setAdapter(viewProductDetailAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        // Xử lý sự kiện khi click vào nút Back
        btnBack.setOnClickListener(v -> finish());

        // Xử lý click add to cart
        addtoCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(productInfo);
            Toast.makeText(this, productInfo.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });

        // Xử lý click buy now
        buynow.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(productInfo);
            Toast.makeText(this, productInfo.getName() + " added to cart!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("fragment_id", R.id.cartFragment);
            startActivity(intent);
        });
    }

}
