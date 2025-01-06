package com.example.myapplication.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.Fragment.DescriptionFragment;
import com.example.myapplication.Fragment.ReviewFragment;

public class ViewProductDetailAdapter extends FragmentStateAdapter {

    private String productDescription;

    public ViewProductDetailAdapter(@NonNull FragmentActivity fragmentActivity, String productDescription) {
        super(fragmentActivity);
        this.productDescription = productDescription;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Truyền dữ liệu vào DescriptionFragment
                DescriptionFragment descriptionFragment = new DescriptionFragment();
                Bundle bundle = new Bundle();
                bundle.putString("description", productDescription);
                descriptionFragment.setArguments(bundle);
                return descriptionFragment;
            case 1:
                return new ReviewFragment();
            default:
                return new DescriptionFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
