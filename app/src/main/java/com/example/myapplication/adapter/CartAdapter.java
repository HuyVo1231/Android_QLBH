package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CartManager;
import com.example.myapplication.model.ProductModel;

import java.util.List;

public class CartAdapter extends BaseAdapter {

    private Context context;
    private List<ProductModel> productList;
    private int layoutResource;
    private OnQuantityChangeListener quantityChangeListener;

    public interface OnQuantityChangeListener {
        void updateCartSummary();
    }

    public CartAdapter(Context context, int layoutResource, List<ProductModel> productList, OnQuantityChangeListener listener) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.productList = productList;
        this.quantityChangeListener = listener;
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
        holder.quantityProduct.setText(String.valueOf(product.getQuantity()));

        holder.btnIncrease.setOnClickListener(v -> updateQuantity(product, 1));
        holder.btnDecrease.setOnClickListener(v -> updateQuantity(product, -1, position));
        holder.btnDelete.setOnClickListener(v -> removeProduct(position, product));

        return convertView;
    }

    private void updateQuantity(ProductModel product, int change) {
        product.setQuantity(product.getQuantity() + change);
        notifyDataSetChanged();
        if (quantityChangeListener != null) {
            quantityChangeListener.updateCartSummary();
        }
    }

    private void updateQuantity(ProductModel product, int change, int position) {
        if (product.getQuantity() + change > 0) {
            product.setQuantity(product.getQuantity() + change);
        } else {
            removeProduct(position, product);
        }
        notifyDataSetChanged();
        if (quantityChangeListener != null) {
            quantityChangeListener.updateCartSummary();
        }
    }

    private void removeProduct(int position, ProductModel product) {
        productList.remove(position);
        CartManager.getInstance().removeFromCart(product);
        notifyDataSetChanged();
        if (quantityChangeListener != null) {
            quantityChangeListener.updateCartSummary();
        }
    }

    // Helper method to format price
    private String formatPrice(double price) {
        return String.format("$%.2f", price);
    }

    private static class ViewHolder {
        ImageView imageProduct, btnIncrease, btnDecrease, btnDelete;
        TextView nameProduct, priceProduct, quantityProduct;

        ViewHolder(View view) {
            imageProduct = view.findViewById(R.id.imageProduct);
            nameProduct = view.findViewById(R.id.nameProduct);
            priceProduct = view.findViewById(R.id.priceProduct);
            quantityProduct = view.findViewById(R.id.quantityProduct);
            btnIncrease = view.findViewById(R.id.btnPlus);
            btnDecrease = view.findViewById(R.id.btnMinus);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
