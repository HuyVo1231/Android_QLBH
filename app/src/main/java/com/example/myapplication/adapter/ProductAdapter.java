package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.ProductDetailActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.CartManager;
import com.example.myapplication.model.ProductModel;

import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<ProductModel> productList;
    private int layoutResource;

    public ProductAdapter(Context context, int layoutResource, List<ProductModel> productList) {
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
            holder = new ViewHolder();
            holder.imageProduct = convertView.findViewById(R.id.imageProduct);
            holder.nameProduct = convertView.findViewById(R.id.nameProduct);
            holder.priceProduct = convertView.findViewById(R.id.priceProduct);
            holder.btnAddToCart = convertView.findViewById(R.id.btnAddToCart);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // Lấy dữ liệu từ Model, và set vào Layout.
        ProductModel product = productList.get(position);

        // Dùng Dlie hiển thị image.
        Glide.with(context)
                .load(product.getImageUrl())
                .into(holder.imageProduct);
        holder.nameProduct.setText(product.getName());
        holder.priceProduct.setText("$" + product.getPrice());

        // Xử lý nút "Add to Cart"
        holder.btnAddToCart.setOnClickListener(v -> {
            // Thêm sản phẩm vào giỏ hàng
            CartManager.getInstance().addToCart(product);
            Toast.makeText(context, product.getName() + " added to cart!", Toast.LENGTH_SHORT).show();
        });

        // Xử lý click tổng thể (chuyển đến ProductDetailActivity)
        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("id", product.getId());
            context.startActivity(intent);
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageProduct;
        TextView nameProduct;
        TextView priceProduct;
        TextView btnAddToCart;
    }
}
