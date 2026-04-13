package com.example.hethongbangiay.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.MainActivity;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.Constants;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class GoogleProfileConfirmActivity extends AppCompatActivity {

    private TextInputEditText edtFullName, edtPhone, edtAddress;
    private MaterialButton btnContinue;

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_profile_confirm);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnContinue = findViewById(R.id.btnContinue);

        prefillGoogleInfo();

        btnContinue.setOnClickListener(v -> saveProfile());
    }

    private void prefillGoogleInfo() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (firebaseUser.getDisplayName() != null) {
                edtFullName.setText(firebaseUser.getDisplayName());
            }
            if (firebaseUser.getPhoneNumber() != null) {
                edtPhone.setText(firebaseUser.getPhoneNumber());
            }
        }

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            if (edtFullName.getText() == null || edtFullName.getText().toString().trim().isEmpty()) {
                edtFullName.setText(account.getDisplayName());
            }
        }
    }

    private void saveProfile() {
        String fullName = edtFullName.getText() != null ? edtFullName.getText().toString().trim() : "";
        String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
        String address = edtAddress.getText() != null ? edtAddress.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            edtFullName.setError("Nhập họ tên");
            edtFullName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }

        if (address.isEmpty()) {
            edtAddress.setError("Nhập địa chỉ");
            edtAddress.requestFocus();
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Không tìm thấy tài khoản Google", Toast.LENGTH_SHORT).show();
            return;
        }

        NguoiDung user = new NguoiDung(
                currentUser.getUid(),
                currentUser.getEmail(),
                fullName,
                phone,
                currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : "",
                Constants.VAI_TRO_USER
        );

        userRepository.saveUserProfile(user)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Lỗi lưu hồ sơ: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show()
                );
    }
}