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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.admin.AdminDashboardActivity;
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.adapters.SanPhamAdapter;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.models.NguoiDung; // Thêm import cho NguoiDung
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.viewmodels.AuthViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Khai báo repository ở cấp độ lớp để tất cả các hàm đều dùng được
    private NguoiDungRepository repository;
    private TextView tvUsername;
    private AuthViewModel authViewModel;

    //Biến lấy dữ liệu db
    private RecyclerView rvProducts;
    private SanPhamAdapter sanPhamAdapter;
    private SanPhamDB sanPhamDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        // 1. Khởi tạo Repository, ViewModel và View
        repository = new NguoiDungRepository();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class); // Khởi tạo ViewModel
        tvUsername = findViewById(R.id.tvUsername);

        rvProducts = findViewById(R.id.rvProducts);

        // Lấy dữ liệu từ database lên
        sanPhamDatabase = new SanPhamDB(this);
//        sanPhamDatabase.taoDuLieuMau();
        sanPhamAdapter = new SanPhamAdapter(this, sanPhamDatabase.layTatCaSpDangActive());

        rvProducts.setAdapter(sanPhamAdapter);

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

            if (id == R.id.nav_home) {
                // Home: Luôn hiển thị, không cần kiểm tra login
                Toast.makeText(this, "Đang ở trang chủ", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_cart) {
                // Cart: Cần login, nhưng chức năng chưa hoàn thiện
                if (!repository.isUserLoggedIn()) {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem giỏ hàng", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return false;
                }
                Toast.makeText(this, "Chức năng giỏ hàng đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_orders) {
                // Orders: Cần login, chức năng chưa hoàn thiện
                if (!repository.isUserLoggedIn()) {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return false;
                }
                Toast.makeText(this, "Chức năng đơn hàng đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_wallet) {
                // Wallet: Cần login, chức năng chưa hoàn thiện
                if (!repository.isUserLoggedIn()) {
                    Toast.makeText(this, "Vui lòng đăng nhập để xem ví", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    return false;
                }
                Toast.makeText(this, "Chức năng ví đang phát triển", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                if (!repository.isUserLoggedIn()) {
                    // CHƯA ĐĂNG NHẬP: Chuyển sang Login
                    Toast.makeText(this, "Vui lòng đăng nhập để xem hồ sơ", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    return false;
                } else {
                    // ĐÃ ĐĂNG NHẬP: Lấy profile hiện tại và kiểm tra role
                    NguoiDung profile = authViewModel.getUserProfile().getValue();
                    if (profile != null) {
                        String role = profile.getVaiTro();
                        if ("ADMIN".equals(role)) {
                            // Admin: Chuyển đến AdminDashboardActivity
                            startActivity(new Intent(MainActivity.this, AdminDashboardActivity.class));
                        } else {
                            // User/Seller: Chuyển đến ProfileActivity
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        }
                    } else {
                        // Profile chưa tải, tải lại
                        FirebaseUser user = repository.getCurrentUser();
                        if (user != null) {
                            authViewModel.loadUserProfile(user.getUid());
                            Toast.makeText(this, "Đang tải profile...", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return false;
                }
            }

            return true;
        });

        // Quan sát role để cập nhật UI (chỉ set một lần trong onCreate)
        authViewModel.getUserProfile().observe(this, profile -> {
            if (profile != null) {
                updateUIBasedOnRole(profile.getVaiTro());
            } else {
                // Chưa đăng nhập, hiển thị Guest
                updateUIBasedOnRole(null);
            }
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
        // Tải lại profile để đảm bảo role được cập nhật
        if (repository.isUserLoggedIn()) {
            FirebaseUser user = repository.getCurrentUser();
            if (user != null) {
                authViewModel.loadUserProfile(user.getUid());
            }
        }
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

    private void updateUIBasedOnRole(String role) {
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        if (role == null) {
            // Guest: chỉ hiển thị Home, ẩn Profile và các tab khác nếu cần
            bottomNavigation.getMenu().findItem(R.id.nav_profile).setVisible(false);
            bottomNavigation.getMenu().findItem(R.id.nav_cart).setVisible(false);
            bottomNavigation.getMenu().findItem(R.id.nav_orders).setVisible(false);
            bottomNavigation.getMenu().findItem(R.id.nav_wallet).setVisible(false);
        } else if ("ADMIN".equals(role)) {
            // Admin: hiển thị tất cả, Profile sẽ dẫn đến Admin Dashboard
            bottomNavigation.getMenu().findItem(R.id.nav_profile).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_cart).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_orders).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_wallet).setVisible(true);
        } else {
            // User/Seller: hiển thị Home, Profile, Cart, Orders, Wallet
            bottomNavigation.getMenu().findItem(R.id.nav_profile).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_cart).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_orders).setVisible(true);
            bottomNavigation.getMenu().findItem(R.id.nav_wallet).setVisible(true);
        }
    }
}