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
import com.example.myapplication.admin.AdminActivityAddCategory;
import com.example.myapplication.admin.AdminActivityAddMenu;
import com.example.myapplication.database.CategoryDatabaseHelper;
import com.example.myapplication.model.CategoryModel;

import java.util.List;

public class AdminCategoryAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryModel> categoryList;
    private int layoutResource;

    public AdminCategoryAdapter(Context context, int layoutResource, List<CategoryModel> categoryList) {
        this.context = context;
        this.layoutResource = layoutResource;
        this.categoryList = categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
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

        CategoryModel category = categoryList.get(position);

        // Lấy dữ liệu và set dữ liệu vào UI.
        Glide.with(context)
                .load(category.getImageUrl())
                .into(holder.imageCategory);
        holder.nameCategory.setText(category.getName());

        // Set OnClickListener for btnDelete
        holder.btnDelete.setOnClickListener(v -> {
            CategoryDatabaseHelper databaseHelper = new CategoryDatabaseHelper(context);
            databaseHelper.open();
            boolean isDeleted = databaseHelper.deleteCategoryById(category.getIdCategory());
            databaseHelper.close();

            if (isDeleted) {
                categoryList.remove(position);
                notifyDataSetChanged();
            }
        });

        // Set OnClickListener for rootLayout
        holder.rootLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminActivityAddCategory.class);
            intent.putExtra("category_id", category.getIdCategory());
            intent.putExtra("name", category.getName());
            intent.putExtra("image_url", category.getImageUrl());
            ((Activity) context).startActivityForResult(intent, 1001);
        });

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageCategory, btnDelete;
        TextView nameCategory;
        View rootLayout;

        ViewHolder(View view) {
            rootLayout = view.findViewById(R.id.rootLayout);
            imageCategory = view.findViewById(R.id.imageCategory);
            nameCategory = view.findViewById(R.id.nameCategory);
            btnDelete = view.findViewById(R.id.btnDelete);
        }
    }
}
