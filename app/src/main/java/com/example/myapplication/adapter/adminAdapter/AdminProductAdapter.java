package com.example.myapplication.adapter.adminAdapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.admin.AdminActivityAddMenu;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.ProductModel;

import java.util.List;

public class AdminProductAdapter extends BaseAdapter {

    private Context context;
    private List<ProductModel> productList;
    private int layoutResource;

    public AdminProductAdapter(Context context, int layoutResource, List<ProductModel> productList) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductModel product = productList.get(position);

        // Lấy dữ liệu và set dữ liệu vào UI.
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.imageProduct);
        holder.nameProduct.setText(product.getName());
        holder.priceProduct.setText(formatPrice(product.getPrice()));

        // Set OnClickListener for btnDelete
        holder.btnDelete.setOnClickListener(v -> {
            ProductDatabaseHelper databaseHelper = new ProductDatabaseHelper(context);
            databaseHelper.open();
            boolean isDeleted = databaseHelper.deleteProductById(product.getId());
            databaseHelper.close();

            if (isDeleted) {
                productList.remove(position);
                notifyDataSetChanged();
            }
        });

        // Set OnClickListener for rootLayout
        holder.rootLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminActivityAddMenu.class);
            intent.putExtra("product_id", product.getId());
            intent.putExtra("product_code", product.getProductCode());
            intent.putExtra("name", product.getName());
            intent.putExtra("price", product.getPrice());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("image_url", product.getImageUrl());
            intent.putExtra("category_id", product.getCategoryId());
            ((Activity) context).startActivityForResult(intent, 1001);
        });

        return convertView;
    }

    // Helper method to format price
    private String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    private static class ViewHolder {
        ImageView imageProduct, btnDelete;
        TextView nameProduct, priceProduct;
        View rootLayout;

        ViewHolder(View view) {
            rootLayout = view.findViewById(R.id.rootLayout);
            imageProduct = view.findViewById(R.id.imageProduct);
            nameProduct = view.findViewById(R.id.nameProduct);
            priceProduct = view.findViewById(R.id.priceProduct);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
