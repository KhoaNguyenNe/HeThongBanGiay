package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Build;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Khai báo repository ở cấp độ lớp để tất cả các hàm đều dùng được
    private NguoiDungRepository repository;
    private TextView tvUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        // 1. Khởi tạo Repository và View
        repository = new NguoiDungRepository();
        tvUsername = findViewById(R.id.tvUsername);

        // --- Cấu hình UI System Bars ---
        setupSystemBars();

        // --- Khởi tạo các View giao diện ---
        View root = findViewById(R.id.main);
        View scrollContent = findViewById(R.id.scrollContent);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        // --- Xử lý Insets (Padding hệ thống cho màn hình tràn viền) ---
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            scrollContent.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            bottomNavigation.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- XỬ LÝ SỰ KIỆN CLICK MENU DƯỚI ---
        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                if (!repository.isUserLoggedIn()) {
                    // CHƯA ĐĂNG NHẬP: Chuyển sang Login
                    Toast.makeText(this, "Vui lòng đăng nhập để xem hồ sơ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return false; // Không chuyển icon sang Profile nếu chưa login
                } else {
                    // ĐÃ ĐĂNG NHẬP: Mở màn hình Profile (Hãy đảm bảo bạn đã tạo Activity này)
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return false; // Trả về false để giữ icon ở Home/tab hiện tại nếu dùng Activity riêng
                }
            }

            // Thêm các xử lý cho Cart hoặc Home ở đây nếu cần
            return true;
        });
    }

    private void setupSystemBars() {
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
            controller.setAppearanceLightNavigationBars(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật thông tin người dùng mỗi khi quay lại màn hình chính
        updateUserUI();
    }

    private void updateUserUI() {
        FirebaseUser user = repository.getCurrentUser();
        if (tvUsername != null) {
            if (user != null) {
                // Ưu tiên hiển thị Tên (DisplayName), nếu không có thì hiện Email
                String name = user.getDisplayName();
                if (name == null || name.isEmpty()) {
                    name = user.getEmail();
                }
                tvUsername.setText(name);
            } else {
                tvUsername.setText("Guest");
            }
        }
    }
}