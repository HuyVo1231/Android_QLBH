package com.example.myapplication.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.adapter.CategoryAdapter;
import com.example.myapplication.database.CategoryDatabaseHelper;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.ProductModel;
import com.example.myapplication.model.CategoryModel;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private ListView listView;
    private ArrayList<ProductModel> productList;
    private RecyclerView recyclerView;
    private ArrayList<CategoryModel> categoryList;
    private CategoryDatabaseHelper categoryDatabaseHelper;
    private ProductDatabaseHelper productDatabaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize database helpers
        categoryDatabaseHelper = new CategoryDatabaseHelper(getContext());
        productDatabaseHelper = new ProductDatabaseHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize product list
        productList = new ArrayList<>();

        // Open the product database to read products
        productDatabaseHelper.open();
        productList = productDatabaseHelper.getAllProducts2();
        // Close the product database connection
        productDatabaseHelper.close();

        // Set up ListView for displaying products
        listView = view.findViewById(R.id.listView);

        // Set the custom adapter for product list
        ProductAdapter productAdapter = new ProductAdapter(getContext(), R.layout.product_item, productList);
        listView.setAdapter(productAdapter);

        // Set up RecyclerView for displaying categories
        recyclerView = view.findViewById(R.id.recyclerViewCategories);

        // Initialize category list
        categoryList = new ArrayList<>();
        categoryDatabaseHelper.open();
        categoryList = categoryDatabaseHelper.getAllCategories();
        // Close the category database connection
        categoryDatabaseHelper.close();

        // Set the layout manager for RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Set the custom adapter for category list
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categoryList);
        recyclerView.setAdapter(categoryAdapter);

        return view;
    }
}
