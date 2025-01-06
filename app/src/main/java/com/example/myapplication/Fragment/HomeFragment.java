package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.myapplication.R;
import com.example.myapplication.ProductDetailActivity;
import com.example.myapplication.adapter.ProductAdapter;
import com.example.myapplication.database.ProductDatabaseHelper;
import com.example.myapplication.model.ProductModel;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

public class HomeFragment extends Fragment {

    private ListView listView;
    private ArrayList<ProductModel> productList;
    private ProductDatabaseHelper databaseHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize database and ImageHelper
        databaseHelper = new ProductDatabaseHelper(getContext());
        databaseHelper.open();

        // Banner
        ImageSlider imageSlider = view.findViewById(R.id.image_slider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.banner1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.banner3, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);

        // ListView setup
        listView = view.findViewById(R.id.listView);

        // Initialize product list with id
        productList = new ArrayList<>();
        productList = databaseHelper.getAllProducts2();

        databaseHelper.close();

        // Set custom adapter
        ProductAdapter adapter = new ProductAdapter(getContext(), R.layout.product_item, productList);
        listView.setAdapter(adapter);

        return view;
    }
}
