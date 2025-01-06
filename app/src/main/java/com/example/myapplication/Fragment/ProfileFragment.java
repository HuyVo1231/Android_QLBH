package com.example.myapplication.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myapplication.ChangeInfoActivity;
import com.example.myapplication.ChangePasswordActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;

public class ProfileFragment extends Fragment {

    private static final int CHANGE_INFO_REQUEST_CODE = 1;

    private TextView userName, userEmail, userPhone, userAddress;
    private Button btnChangePassword, btnChangeInfo;
    private ImageView userImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find views
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userPhone = view.findViewById(R.id.userNumberPhone);
        userAddress = view.findViewById(R.id.userAddress);
        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnChangeInfo = view.findViewById(R.id.btnChangeInfo);
        userImage = view.findViewById(R.id.userImage);

        // Set click listeners
        btnChangePassword.setOnClickListener(v -> handleChangePasswordClick());
        btnChangeInfo.setOnClickListener(v -> handleChangeInfoClick());

        // Load user data
        loadUserData();

        return view;
    }

    private void handleChangePasswordClick() {
        // Logics for changing password
        Toast.makeText(getContext(), "Đổi mật khẩu!", Toast.LENGTH_SHORT).show();

        // For example, navigate to ChangePasswordActivity
        Intent intent = new Intent(getContext(), ChangePasswordActivity.class);
        startActivity(intent);
    }

    private void handleChangeInfoClick() {
        // Logics for changing user info
        Toast.makeText(getContext(), "Thay đổi thông tin người dùng!", Toast.LENGTH_SHORT).show();

        // For example, navigate to ChangeInfoActivity
        Intent intent = new Intent(getContext(), ChangeInfoActivity.class);
        startActivityForResult(intent, CHANGE_INFO_REQUEST_CODE);
    }

    private void loadUserData() {
        // Get info user from UserManager.
        User user = UserManager.getInstance().getUser();

        if (user != null) {
            userName.setText(user.getFullName() != null && !user.getFullName().isEmpty() ? user.getFullName() : "Chưa thêm tên đầy đủ");
            userEmail.setText(user.getEmail() != null && !user.getEmail().isEmpty() ? user.getEmail() : "Chưa thêm email");
            userPhone.setText(user.getPhone() != null && !user.getPhone().isEmpty() ? user.getPhone() : "Chưa thêm số điện thoại");
            userAddress.setText(user.getAddress() != null && !user.getAddress().isEmpty() ? user.getAddress() : "Chưa thêm địa chỉ");

            // Load user image using Glide
            Glide.with(getContext())
                    .load(user.getImageUrl())
                    .into(userImage);
        }
    }

    // Load lại dữ liệu sau khi quay lại.
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_INFO_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            // Load lại
            loadUserData();
        }
    }
}
