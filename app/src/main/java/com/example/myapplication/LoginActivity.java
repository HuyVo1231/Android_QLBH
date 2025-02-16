package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.myapplication.database.UserDatabaseHelper;
import com.example.myapplication.model.User;
import com.example.myapplication.model.UserManager;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    Button buttonLogin;
    EditText editTextPhone, editTextPassword;
    CheckBox checkBoxRemember;
    TextView toRegister, textBiometricLogin;
    UserDatabaseHelper dbHelper;
    SharedPreferncesClass sharedPreferencesClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        dbHelper = new UserDatabaseHelper(this);
        sharedPreferencesClass = new SharedPreferncesClass(this);

        initializeViews();

        // Load thông tin đăng nhập
        loadSavedLoginDetails();

        buttonLogin.setOnClickListener(this);
        toRegister.setOnClickListener(this);
        textBiometricLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonLogin:
                checkLogin();
                break;
            case R.id.toRegister:
                ToRegister();
                break;
            case R.id.textBiometricLogin:
                checkBiometricSupportAndLogin();
                break;
        }
    }

    private void initializeViews() {
        buttonLogin = findViewById(R.id.buttonLogin);
        toRegister = findViewById(R.id.toRegister);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPassword = findViewById(R.id.editTextPassword);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);
        textBiometricLogin = findViewById(R.id.textBiometricLogin);
    }

    public void ToRegister() {
        Intent registerIntent = new Intent(this, SignupActivity.class);
        startActivity(registerIntent);
    }

    public void checkLogin() {
        String phone = editTextPhone.getText().toString();
        String password = editTextPassword.getText().toString();

        if (phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.open();
        User user = dbHelper.login(phone, password);

        if (user != null) {
            handleSuccessfulLogin(user, phone, password);
        } else {
            Toast.makeText(this, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show();
        }

        dbHelper.close();
    }

    private void loadSavedLoginDetails() {
        String savedUserName = sharedPreferencesClass.getUserName();
        String savedPassword = sharedPreferencesClass.getPassword();
        boolean isRemembered = sharedPreferencesClass.getRememberMe(); // Lấy trạng thái checkbox đã lưu

        if (!savedUserName.isEmpty()) {
            editTextPhone.setText(savedUserName);
        }

        if (isRemembered) { // Nếu checkbox được check trước đó thì hiển thị cả mật khẩu
            editTextPassword.setText(savedPassword);
            checkBoxRemember.setChecked(true);
        }
    }

    private void checkBiometricSupportAndLogin() {
        BiometricManager biometricManager = BiometricManager.from(this);

        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                showBiometricPrompt();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Thiết bị không hỗ trợ sinh trắc học", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Phần cứng sinh trắc học không khả dụng", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Chưa đăng ký thông tin sinh trắc học", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, "Không thể sử dụng sinh trắc học", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                handleBiometricLogin();
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(LoginActivity.this, "Lỗi: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(LoginActivity.this, "Xác thực thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Xác thực sinh trắc học")
                .setSubtitle("Đăng nhập bằng vân tay hoặc khuôn mặt của bạn")
                .setNegativeButtonText("Hủy")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void handleBiometricLogin() {
        dbHelper.open();
        String savedPhone = sharedPreferencesClass.getUserName();
        String savedPassword = sharedPreferencesClass.getPassword();

        if (!savedPhone.isEmpty() && !savedPassword.isEmpty()) {
            User user = dbHelper.login(savedPhone, savedPassword);
            if (user != null) {
                handleSuccessfulLogin(user, savedPhone, savedPassword);
            } else {
                Toast.makeText(this, "Tài khoản không hợp lệ!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin đăng nhập đã lưu!", Toast.LENGTH_SHORT).show();
        }
        dbHelper.close();
    }

    private void handleSuccessfulLogin(User user, String phone, String password) {
        UserManager.getInstance().setUser(user);

        // Luôn lưu thông tin đăng nhập
        sharedPreferencesClass.saveLoginDetails(phone, password);

        // Lưu trạng thái checkbox vào SharedPreferences
        sharedPreferencesClass.setRememberMe(checkBoxRemember.isChecked());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
