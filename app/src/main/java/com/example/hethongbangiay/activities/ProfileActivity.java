package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.viewmodels.ProfileViewModel;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvHoTen;
    private TextView tvEmail;
    private TextView tvSoDienThoai;
    private ImageView imgAvatar;
    private Button btnLogout;
    private Button btnEditProfile;

    private ProfileViewModel profileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        observeViewModel();
        profileViewModel.loadProfile();
    }

    private void initViews() {
        tvHoTen = findViewById(R.id.tvHoTen);
        tvEmail = findViewById(R.id.tvEmail);
        tvSoDienThoai = findViewById(R.id.tvSoDienThoai);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnLogout = findViewById(R.id.btnLogout);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));

        btnLogout.setOnClickListener(v -> {
            profileViewModel.logout();
            finish();
        });
    }

    private void observeViewModel() {
        profileViewModel.getProfile().observe(this, this::bindProfile);
        profileViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(ProfileActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        profileViewModel.getLoading().observe(this, isLoading -> {
            btnLogout.setEnabled(!Boolean.TRUE.equals(isLoading));
        });
    }

    private void bindProfile(NguoiDung nguoiDung) {
        if (nguoiDung == null) {
            return;
        }

        tvHoTen.setText(nguoiDung.getHoTen() != null && !nguoiDung.getHoTen().isEmpty()
                ? nguoiDung.getHoTen()
                : "Chưa cập nhật");

        tvEmail.setText("Email: " + (nguoiDung.getEmail() != null ? nguoiDung.getEmail() : "Chưa cập nhật"));
        tvSoDienThoai.setText("SĐT: " + (nguoiDung.getSoDienThoai() != null && !nguoiDung.getSoDienThoai().isEmpty()
                ? nguoiDung.getSoDienThoai()
                : "Chưa cập nhật"));

        if (nguoiDung.getAvatar() != null && !nguoiDung.getAvatar().isEmpty()) {
            Glide.with(this)
                    .load(nguoiDung.getAvatar())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(imgAvatar);
        }
    }
}