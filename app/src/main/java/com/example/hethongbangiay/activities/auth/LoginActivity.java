package com.example.hethongbangiay.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.MainActivity;
import com.example.hethongbangiay.activities.PaymentMethodActivity;
import com.example.hethongbangiay.session.SessionManager;
import com.example.hethongbangiay.viewmodels.AuthViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private MaterialButton btnLogin;
    private MaterialButton btnGoogle;
    private TextView txtForgotPassword;
    private TextView txtSignUp;

    private AuthViewModel authViewModel;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        initViews();
        initActions();
        observeViewModel();
        authViewModel.loadCurrentUserProfile();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        txtSignUp = findViewById(R.id.txtSignUp);
    }

    private void initActions() {
        btnLogin.setOnClickListener(v -> handleLogin());

        btnGoogle.setOnClickListener(v -> {
            if (googleSignInClient != null) {
                googleLauncher.launch(googleSignInClient.getSignInIntent());
            }
        });

        txtForgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        txtSignUp.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));

        setupGoogleSignIn();
    }

    private void observeViewModel() {
        authViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                btnLogin.setEnabled(!isLoading);
                btnGoogle.setEnabled(!isLoading);
            }
        });

        authViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        authViewModel.getUserProfile().observe(this, profile -> {
            if (profile != null) {
                navigateToMain();
            }
        });
    }

    private void handleLogin() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

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

        authViewModel.login(email, password);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, options);

        googleLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        try {
                            GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(result.getData())
                                    .getResult(ApiException.class);
                            if (account != null && account.getIdToken() != null) {
                                authViewModel.loginWithGoogle(account.getIdToken());
                            }
                        } catch (ApiException e) {
                            Toast.makeText(this, "Lỗi Google Sign-in: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void navigateToMain() {
        SessionManager sessionManager = new SessionManager(this);
        if (sessionManager.dangChoXuLyThanhToan()) {
            startActivity(new Intent(LoginActivity.this, PaymentMethodActivity.class));
        } else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        finish();
    }
}
