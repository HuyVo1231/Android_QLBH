package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.model.Address;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;
import com.example.myapplication.database.AddressDatabaseHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddressActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private TextInputEditText recipientName, phoneNumber, addressDelivery, note;
    private Button btnSaveAddress;
    private AddressDatabaseHelper addressDatabaseHelper;
    private Integer idAddress = null;
    private ImageView arrowBack;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        initializeViews();
        addressDatabaseHelper = new AddressDatabaseHelper(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        requestLocationPermission();
        loadData();
        setupListeners();
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    fetchAddressFromLocation(location);
                } else {
                    showToast("Không thể lấy vị trí hiện tại!");
                }
            }).addOnFailureListener(e -> showToast("Lỗi khi lấy vị trí: " + e.getMessage()));
        } else {
            showToast("Chưa cấp quyền truy cập vị trí!");
        }
    }

    private void fetchAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<android.location.Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String fullAddress = addresses.get(0).getAddressLine(0);
                addressDelivery.setText(fullAddress);
            } else {
                showToast("Không tìm thấy địa chỉ từ vị trí hiện tại!");
            }
        } catch (IOException e) {
            showToast("Lỗi khi lấy địa chỉ: " + e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                showToast("Quyền truy cập vị trí bị từ chối!");
            }
        }
    }

    private void initializeViews() {
        recipientName = findViewById(R.id.recipientName);
        phoneNumber = findViewById(R.id.phoneNumber);
        addressDelivery = findViewById(R.id.address);
        note = findViewById(R.id.note);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
        arrowBack = findViewById(R.id.arrowBack);
    }

    private void setupListeners() {
        btnSaveAddress.setOnClickListener(v -> {
            if (idAddress != null && idAddress != -1) {
                updateAddress();
            } else {
                addNewAddress();
            }
        });
    }

    private void loadData() {
        Intent intent = getIntent();
        idAddress = intent.getIntExtra("idAddress", -1);

        if (idAddress != -1) {
            recipientName.setText(intent.getStringExtra("recipientName"));
            phoneNumber.setText(intent.getStringExtra("phone"));
            addressDelivery.setText(intent.getStringExtra("address"));
            note.setText(intent.getStringExtra("note"));
            btnSaveAddress.setText("Chỉnh sửa địa chỉ");
        } else {
            btnSaveAddress.setText("Thêm địa chỉ mới");
        }

        arrowBack.setOnClickListener(v -> backScreen());
    }

    private void addNewAddress() {
        User currentUser = UserManager.getInstance().getUser();
        Integer userId = currentUser.getId();

        String recipient = recipientName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String address = addressDelivery.getText().toString().trim();
        String additionalNote = note.getText().toString().trim();

        if (!validateInputs(recipient, phone, address)) return;

        Address newAddress = new Address(0, userId, recipient, phone, address, additionalNote);

        try {
            addressDatabaseHelper.open();
            long rowId = addressDatabaseHelper.addAddress(newAddress);
            addressDatabaseHelper.close();

            if (rowId != -1) {
                showToast("Địa chỉ đã được lưu!");
                backScreen();
            } else {
                showToast("Lỗi khi lưu địa chỉ. Thử lại sau.");
            }
        } catch (Exception e) {
            showToast("Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    private void updateAddress() {
        String recipient = recipientName.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String address = addressDelivery.getText().toString().trim();
        String additionalNote = note.getText().toString().trim();

        if (!validateInputs(recipient, phone, address)) return;

        Address updatedAddress = new Address(idAddress, 0, recipient, phone, address, additionalNote);

        try {
            addressDatabaseHelper.open();
            boolean isUpdated = addressDatabaseHelper.updateAddress(updatedAddress);
            addressDatabaseHelper.close();

            if (isUpdated) {
                showToast("Địa chỉ đã được cập nhật!");
                backScreen();
            } else {
                showToast("Lỗi khi cập nhật địa chỉ. Thử lại sau.");
            }
        } catch (Exception e) {
            showToast("Lỗi kết nối cơ sở dữ liệu!");
        }
    }

    private boolean validateInputs(String recipient, String phone, String address) {
        if (recipient.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            showToast("Vui lòng nhập đầy đủ thông tin!");
            return false;
        }
        if (!isValidPhoneNumber(phone)) {
            showToast("Số điện thoại không hợp lệ!");
            return false;
        }
        return true;
    }

    private boolean isValidPhoneNumber(String phone) {
        String regex = "^[0-9]{10,15}$";
        return phone.matches(regex);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void backScreen() {
        setResult(RESULT_OK, new Intent());
        finish();
    }
}
