// AdapterProduct.java
package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import com.example.myapplication.R;
import com.example.myapplication.model.Product;
import com.example.myapplication.productEdit;

import java.util.List;

public class AdapterProduct extends ArrayAdapter<Product> {
    private Context context;
    private List<Product> products;
    private ActivityResultLauncher<Intent> productActivityLauncher;

    public AdapterProduct(Context context, List<Product> products, ActivityResultLauncher<Intent> launcher) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
        this.productActivityLauncher = launcher;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.product_item2, parent, false);
        }

        Product product = getItem(position);
        LinearLayout layout = convertView.findViewById(R.id.layout_root);
        TextView nameTextView = convertView.findViewById(R.id.nameproduct);
        TextView priceTextView = convertView.findViewById(R.id.product_price);
        TextView idTextView = convertView.findViewById(R.id.idproduct);

        nameTextView.setText(product.getName());
        priceTextView.setText(String.valueOf(product.getPrice()));
        idTextView.setText(String.valueOf(product.getId()));

        layout.setOnClickListener(v -> {
            Intent intent = new Intent(context, productEdit.class);

            // Truyền các giá trị qua Intent
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_name", product.getName());
            intent.putExtra("product_price", product.getPrice());

            productActivityLauncher.launch(intent);
        });

        return convertView;
    }
}
