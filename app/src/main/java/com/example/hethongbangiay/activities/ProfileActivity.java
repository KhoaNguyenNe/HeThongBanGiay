package com.example.hethongbangiay.activities;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvHoTen, tvEmail, tvSoDienThoai;
    private ImageView imgAvatar;
    private Button btnLogout;
    private NguoiDungRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        repository = new NguoiDungRepository();

        loadUserProfile();
    }

    private void initViews() {
        tvHoTen = findViewById(R.id.tvHoTen);
        tvEmail = findViewById(R.id.tvEmail);
        tvSoDienThoai = findViewById(R.id.tvSoDienThoai);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnLogout = findViewById(R.id.btnLogout);

        btnLogout.setOnClickListener(v -> {
            repository.logout();
            finish(); // Quay lại MainActivity
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = repository.getCurrentUser();
        if (currentUser == null) return;

        // 1. Hiển thị email từ Auth trước
        tvEmail.setText(currentUser.getEmail());

        // 2. Lấy dữ liệu chi tiết từ Firestore
        repository.getThongTinChiTiet(currentUser.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Lấy các trường dữ liệu (Đảm bảo tên trường khớp với Firestore)
                String hoTen = documentSnapshot.getString("hoTen");
                String sdt = documentSnapshot.getString("soDienThoai");
                String avatarUrl = documentSnapshot.getString("avatar"); // Link ảnh Cloudinary

                if (hoTen != null) tvHoTen.setText(hoTen);
                if (sdt != null) tvSoDienThoai.setText("SĐT: " + sdt);

                // Sử dụng Glide để load ảnh từ link Cloudinary
                if (avatarUrl != null && !avatarUrl.isEmpty()) {
                    Glide.with(this)
                            .load(avatarUrl)
                            .placeholder(R.drawable.ic_launcher_background) // Ảnh mặc định khi đang load
                            .into(imgAvatar);
                }
            }
        });
    }
}