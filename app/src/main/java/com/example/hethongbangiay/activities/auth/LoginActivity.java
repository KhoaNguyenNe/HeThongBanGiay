package com.example.hethongbangiay.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.MainActivity;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private CheckBox cbRemember;
    private TextView txtForgotPassword, txtSignUp;
    private MaterialButton btnLogin, btnGoogle, btnFacebook;
    private ProgressBar progressBar;

    // Sử dụng Repository thay vì gọi trực tiếp FirebaseAuth
    private NguoiDungRepository nguoiDungRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nguoiDungRepository = new NguoiDungRepository();

        initViews();
        initActions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra nếu đã đăng nhập trước đó
        FirebaseUser currentUser = nguoiDungRepository.getCurrentUser();
        if (currentUser != null) {
            navigateToMain();
        }
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        cbRemember = findViewById(R.id.cbRemember);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtSignUp = findViewById(R.id.txtSignUp);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void initActions() {
        btnLogin.setOnClickListener(v -> handleLogin());

        txtForgotPassword.setOnClickListener(v -> {
            // Chuyển sang màn hình quên mật khẩu (cần tạo class này)
            // Intent intent = new Intent(this, ForgotPasswordActivity.class);
            // startActivity(intent);
        });

        txtSignUp.setOnClickListener(v -> {
            // Chuyển sang màn hình đăng ký (cần tạo class này)
            // Intent intent = new Intent(this, RegisterActivity.class);
            // startActivity(intent);
        });
    }

    private void handleLogin() {
        String email = String.valueOf(edtEmail.getText()).trim();
        String password = String.valueOf(edtPassword.getText()).trim();

        // 1. Kiểm tra đầu vào (Validation)
        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        // 2. Hiển thị trạng thái đang xử lý
        setLoading(true);

        // 3. Gọi Repository để đăng nhập
        nguoiDungRepository.login(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getLocalizedMessage() : "Lỗi đăng nhập không xác định";
                        Toast.makeText(LoginActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Hàm tiện ích để quản lý UI khi đang tải
    private void setLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateToMain() {
        if (getCallingActivity() != null) {
            setResult(RESULT_OK); // Trả kết quả thành công cho màn hình gọi nó
            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}