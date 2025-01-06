package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.model.Address;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;
import com.example.myapplication.database.AddressDatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class AddressActivity extends AppCompatActivity {

    // Declare variables for TextInputEditTexts and Button
    private TextInputEditText recipientName, phoneNumber, addressDelivery, note;
    private Button btnSaveAddress;
    private AddressDatabaseHelper addressDatabaseHelper;
    private Integer idAddress = null;
    private ImageView btnBackScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        // Initialize the views and database helper
        initializeViews();
        addressDatabaseHelper = new AddressDatabaseHelper(this);
        setupListeners();

        // Load data for edit.
        loadData();
    }


    private void setupListeners() {
        btnSaveAddress.setOnClickListener(v -> {
            if (idAddress != null && idAddress != -1) {
                updateAddress(); // Chỉnh sửa địa chỉ
            } else {
                addNewAddress(); // Thêm mới địa chỉ
            }
        });

        btnBackScreen.setOnClickListener(v -> backScreen());
    }

    private void addNewAddress() {
        // Get current user information
        User currentUser = UserManager.getInstance().getUser();
        Integer userId = currentUser.getId();

        // Get the input values from the TextInputEditText fields
        String recipient = recipientName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String address = addressDelivery.getText().toString().trim();
        String additionalNote = note.getText().toString().trim();

        // Validate the inputs
        if (recipient.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create an Address object
        Address newAddress = new Address(0, userId, recipient, phone, address, additionalNote);

        // Open the database and add the new address
        try {
            addressDatabaseHelper.open();
            long rowId = addressDatabaseHelper.addAddress(newAddress);
            addressDatabaseHelper.close();

            // Check if the address was added successfully
            if (rowId != -1) {
                Toast.makeText(this, "Địa chỉ đã được lưu!", Toast.LENGTH_SHORT).show();
                backScreen();
            } else {
                Toast.makeText(this, "Lỗi khi lưu địa chỉ. Thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAddress() {
        // Lấy thông tin từ các trường nhập liệu
        String recipient = recipientName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String address = addressDelivery.getText().toString().trim();
        String additionalNote = note.getText().toString().trim();

        // Kiểm tra dữ liệu hợp lệ
        if (recipient.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPhoneNumber(phone)) {
            Toast.makeText(this, "Số điện thoại không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng Address mới
        Address updatedAddress = new Address(idAddress, 0, recipient, phone, address, additionalNote);

        // Mở cơ sở dữ liệu và thực hiện cập nhật
        try {
            addressDatabaseHelper.open();
            boolean isUpdated = addressDatabaseHelper.updateAddress(updatedAddress);
            addressDatabaseHelper.close();

            if (isUpdated) {
                Toast.makeText(this, "Địa chỉ đã được cập nhật!", Toast.LENGTH_SHORT).show();
                backScreen();
            } else {
                Toast.makeText(this, "Lỗi khi cập nhật địa chỉ. Thử lại sau.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi kết nối cơ sở dữ liệu!", Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeViews() {
        recipientName = findViewById(R.id.recipientName);
        phoneNumber = findViewById(R.id.phoneNumber);
        addressDelivery = findViewById(R.id.address);
        note = findViewById(R.id.note);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        btnBackScreen = findViewById(R.id.arrowBack);
    }

    private void loadData() {
        Intent intent = getIntent();
        idAddress = intent.getIntExtra("idAddress", -1);

        if (idAddress != -1) { // Nếu idAddress tồn tại
            String recipient = intent.getStringExtra("recipientName");
            String phone = intent.getStringExtra("phone");
            String address = intent.getStringExtra("address");
            String noteText = intent.getStringExtra("note");
            recipientName.setText(recipient);
            phoneNumber.setText(phone);
            addressDelivery.setText(address);
            note.setText(noteText);

            btnSaveAddress.setText("Chỉnh sửa địa chỉ");
        } else {
            btnSaveAddress.setText("Thêm địa chỉ mới");
        }
    }

    private void backScreen() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private boolean isValidPhoneNumber(String phone) {
        String regex = "^[0-9]{10,15}$";
        return phone.matches(regex);
    }


}
