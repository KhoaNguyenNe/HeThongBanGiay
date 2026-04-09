package com.example.hethongbangiay.activities.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.viewmodels.AuthViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private MaterialButton btnSendReset;
    private TextView txtBackToLogin;

    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        initActions();
        observeViewModel();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        btnSendReset = findViewById(R.id.btnSendReset);
        txtBackToLogin = findViewById(R.id.txtBackToLogin);
    }

    private void initActions() {
        btnSendReset.setOnClickListener(v -> {
            String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                edtEmail.setError("Email không hợp lệ");
                edtEmail.requestFocus();
                return;
            }
            authViewModel.sendPasswordReset(email);
        });

        txtBackToLogin.setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        authViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                btnSendReset.setEnabled(!isLoading);
            }
        });
    }
}