package com.example.hethongbangiay.activities.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class GoogleProfileConfirmActivity extends AppCompatActivity {

    private TextInputEditText edtFullName, edtPhone, edtAddress;
    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_profile_confirm);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtAddress = findViewById(R.id.edtAddress);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String fullName = String.valueOf(edtFullName.getText()).trim();
        String phone = String.valueOf(edtPhone.getText()).trim();
        String address = String.valueOf(edtAddress.getText()).trim();

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

        Toast.makeText(this, "Cập nhật hồ sơ thành công", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}