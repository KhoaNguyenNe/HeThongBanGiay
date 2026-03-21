package com.example.hethongbangiay;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private MaterialButton btnSendReset;
    private TextView txtBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        edtEmail = findViewById(R.id.edtEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);

        btnSendReset.setOnClickListener(v -> sendResetEmail());

        txtBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void sendResetEmail() {
        String email = String.valueOf(edtEmail.getText()).trim();

        if (email.isEmpty()) {
            edtEmail.setError("Nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        Toast.makeText(this, "Đã gửi liên kết đặt lại mật khẩu", Toast.LENGTH_SHORT).show();
    }
}