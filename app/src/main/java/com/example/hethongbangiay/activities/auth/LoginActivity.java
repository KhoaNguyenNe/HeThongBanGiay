package com.example.hethongbangiay.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.MainActivity;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private CheckBox cbRemember;
    private TextView txtForgotPassword, txtSignUp;
    private MaterialButton btnLogin, btnGoogle, btnFacebook;
    private ProgressBar progressBar;

    private NguoiDungRepository nguoiDungRepository;
    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nguoiDungRepository = new NguoiDungRepository();

        // 1. Cấu hình Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 2. Đăng ký xử lý kết quả trả về từ Google
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                            if (account != null) {
                                firebaseAuthWithGoogle(account.getIdToken());
                            }
                        } catch (ApiException e) {
                            Toast.makeText(this, "Lỗi đăng nhập Google: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        initViews();
        initActions();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        // Đăng nhập Email/Password
        btnLogin.setOnClickListener(v -> handleLogin());

        // Đăng nhập Google - FIX: Thêm sự kiện click
        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent);
        });

        // Chuyển sang màn hình Đăng ký
        txtSignUp.setOnClickListener(v -> {
            // Intent intent = new Intent(this, RegisterActivity.class);
            // startActivity(intent);
            Toast.makeText(this, "Tính năng đăng ký đang phát triển", Toast.LENGTH_SHORT).show();
        });

        txtForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng quên mật khẩu đang phát triển", Toast.LENGTH_SHORT).show();
        });
    }

    private void handleLogin() {
        String email = String.valueOf(edtEmail.getText()).trim();
        String password = String.valueOf(edtPassword.getText()).trim();

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

        setLoading(true);
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

    // FIX: Thêm hàm xác thực Token Google với Firebase
    private void firebaseAuthWithGoogle(String idToken) {
        setLoading(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();
                        navigateToMain();
                    } else {
                        String error = task.getException() != null ? task.getException().getMessage() : "Lỗi xác thực";
                        Toast.makeText(this, "Lỗi Firebase: " + error, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean isLoading) {
        btnLogin.setEnabled(!isLoading);
        btnGoogle.setEnabled(!isLoading);
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void navigateToMain() {
        if (getCallingActivity() != null) {
            setResult(RESULT_OK);
            finish();
        } else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}