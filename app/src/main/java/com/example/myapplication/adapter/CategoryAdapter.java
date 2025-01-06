package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.model.CategoryModel;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<CategoryModel> categoryList;

    // Constructor
    public CategoryAdapter(Context context, List<CategoryModel> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        // Get the current category
        CategoryModel category = categoryList.get(position);
        String imageUrl = category.getImageUrl();

        // Sử dụng Glide để tải ảnh từ URL vào ImageView
        Glide.with(context)
                .load(imageUrl)  // URL của hình ảnh
                .into(holder.imageCategory);  // Đưa vào ImageView

        holder.nameCategory.setText(category.getName());
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // ViewHolder class
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageCategory;
        TextView nameCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            imageCategory = itemView.findViewById(R.id.imageCategory);
            nameCategory = itemView.findViewById(R.id.nameCategory);
        }
    }
}
