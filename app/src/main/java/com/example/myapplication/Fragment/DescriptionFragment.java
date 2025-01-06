package com.example.myapplication.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;

public class DescriptionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_description, container, false);

        // Lấy description từ arguments
        Bundle bundle = getArguments();
        if (bundle != null) {
            String description = bundle.getString("description");
            // Gắn dữ liệu vào TextView.
            TextView descriptionText = view.findViewById(R.id.textDescription);
            descriptionText.setText(description);
        }
        return view;

    }
}