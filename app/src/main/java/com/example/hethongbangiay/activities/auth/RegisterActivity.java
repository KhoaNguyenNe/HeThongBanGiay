package com.example.hethongbangiay.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.MainActivity;
import com.example.hethongbangiay.activities.PaymentMethodActivity;
import com.example.hethongbangiay.session.SessionManager;
import com.example.hethongbangiay.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtFullName;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private TextInputEditText edtConfirmPassword;
    private MaterialButton btnRegister;
    private TextView txtSignIn;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        initActions();
        observeViewModel();
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtSignIn = findViewById(R.id.txtSignIn);
    }

    private void initActions() {
        btnRegister.setOnClickListener(v -> registerAccount());

        txtSignIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void observeViewModel() {
        authViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                btnRegister.setEnabled(!isLoading);
            }
        });

        authViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getUserProfile().observe(this, profile -> {
            if (profile != null) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                SessionManager sessionManager = new SessionManager(this);
                if (sessionManager.dangChoXuLyThanhToan()) {
                    startActivity(new Intent(RegisterActivity.this, PaymentMethodActivity.class));
                } else {
                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                }
                finish();
            }
        });
    }

    private void registerAccount() {
        String fullName = edtFullName.getText() != null ? edtFullName.getText().toString().trim() : "";
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";
        String confirmPassword = edtConfirmPassword.getText() != null ? edtConfirmPassword.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            edtFullName.setError("Nhập họ tên");
            edtFullName.requestFocus();
            return;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu tối thiểu 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu không khớp");
            edtConfirmPassword.requestFocus();
            return;
        }

        authViewModel.register(fullName, email, password);
    }
}
