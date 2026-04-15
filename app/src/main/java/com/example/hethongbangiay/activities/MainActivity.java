package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.adapters.DanhMucAdapter;
import com.example.hethongbangiay.adapters.SanPhamAdapter;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.utils.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.repositories.DanhMucRepository;
import com.example.hethongbangiay.firestore.FirebaseMigrationSeeder;
import com.example.hethongbangiay.repositories.SanPhamRepository;

public class MainActivity extends AppCompatActivity {

    // Khai báo repository ở cấp độ lớp để tất cả các hàm đều dùng được
    private NguoiDungRepository repository;
    private TextView tvUsername;
    private TextView tvPopularTitle;

    //Biến lấy dữ liệu db của Sp
    private RecyclerView rvProducts;
    private SanPhamAdapter sanPhamAdapter;
    private SanPhamRepository sanPhamRepository;
    //Danh mục
    private RecyclerView rvCategories;
    private DanhMucAdapter danhMucAdapter;
    private DanhMucRepository danhMucRepository;

    //Tìm kiếm
    private MaterialCardView searchContainer;
    private EditText edtSearch;
    private String danhMucDangChon = null;
    private double giaMaxTrangChu = 0;
    private View scrollContent;
    private View fragmentContainer;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        new FirebaseMigrationSeeder(this).migrateAll(
                () -> runOnUiThread(() -> Toast.makeText(this, "Đã migrate SQLite -> Firestore", Toast.LENGTH_SHORT).show()),
                e -> runOnUiThread(() -> Toast.makeText(this, "Lỗi migrate: " + e.getMessage(), Toast.LENGTH_SHORT).show())
        );

        // 1. Khởi tạo Repository và View
        repository = new NguoiDungRepository();
        tvUsername = findViewById(R.id.tvUsername);
        tvPopularTitle = findViewById(R.id.tvPopularTitle);

        //Lấy dữ liệu Sản phẩm từ db
        rvProducts = findViewById(R.id.rvProducts);
        sanPhamRepository = new SanPhamRepository();
        sanPhamAdapter = new SanPhamAdapter(this, new java.util.ArrayList<>(), sp -> {
            Intent myIntent = new Intent(MainActivity.this, ProductDetailActivity.class);
            myIntent.putExtra(ProductDetailActivity.EXTRA_SAN_PHAM_ID, sp.getSanPhamId());
            startActivity(myIntent);
        });
        rvProducts.setAdapter(sanPhamAdapter);

    // Lấy dữ liệu Danh mục từ Firestore
        rvCategories = findViewById(R.id.rvCategories);
        danhMucRepository = new DanhMucRepository();
        danhMucAdapter = new DanhMucAdapter(this, new java.util.ArrayList<>(), danhMuc -> {
            if (danhMuc.getDanhMucId().equals(danhMucDangChon)) {
                danhMucDangChon = null;
            } else {
                danhMucDangChon = danhMuc.getDanhMucId();
            }

            danhMucAdapter.setSelectedDanhMucId(danhMucDangChon);
            taiSanPhamTrangChu();
        });
        rvCategories.setAdapter(danhMucAdapter);

        danhMucRepository.layTatCaDMActive(new OnFirestoreResult<java.util.List<com.example.hethongbangiay.models.DanhMuc>>() {
            @Override
            public void onSuccess(java.util.List<com.example.hethongbangiay.models.DanhMuc> data) {
                danhMucAdapter.capNhatDuLieu(data);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "Không tải được danh mục", Toast.LENGTH_SHORT).show();
            }
        });

        //Khai báo biến tìm kiếm
        searchContainer = findViewById(R.id.searchContainer);
        edtSearch = findViewById(R.id.edtSearch);
        sanPhamRepository.layGiaMax(new OnFirestoreResult<Double>() {
            @Override
            public void onSuccess(Double data) {
                giaMaxTrangChu = data == null ? 0 : data;
                taiSanPhamTrangChu();
            }

            @Override
            public void onError(Exception e) {
                giaMaxTrangChu = 0;
            }
        });
        setupHomeSearch();

        // --- Cấu hình UI System Bars ---
        ThemeUtils.applySystemBars(this);

        // --- Khởi tạo các View giao diện ---
        View root = findViewById(R.id.main);
        scrollContent = findViewById(R.id.scrollContent);
        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fragmentContainer.setVisibility(View.GONE);

        // --- Xử lý Insets (Padding hệ thống cho màn hình tràn viền) ---
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            
            // Lấy chiều cao của Bottom Navigation để tránh đè nội dung
            int bottomNavHeight = bottomNavigation.getHeight();
            if (bottomNavHeight <= 0) {
                bottomNavHeight = (int) (60 * getResources().getDisplayMetrics().density);
            }

            int totalBottomPadding = bottomNavHeight + systemBars.bottom;

            scrollContent.setPadding(systemBars.left, systemBars.top, systemBars.right, totalBottomPadding);
            fragmentContainer.setPadding(systemBars.left, systemBars.top, systemBars.right, totalBottomPadding);
            
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
                    // ĐÃ ĐĂNG NHẬP: Mở màn hình Profile
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    return false; // Trả về false để giữ icon ở Home/tab hiện tại nếu dùng Activity riêng
                }
            }

            if (id == R.id.nav_home) {
                hienTrangChu();
                return true;
            }

            if (id == R.id.nav_cart) {
                moFragment(new CartFragment());
                return true;
            }

            if (id == R.id.nav_orders) {
                moFragment(new OrdersFragment());
                return true;
            }

            return true;
        });

        taiSanPhamTrangChu();
        xuLyIntentDieuHuong(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật thông tin người dùng mỗi khi quay lại màn hình chính
        updateUserUI();
        taiSanPhamTrangChu();
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
                tvUsername.setText("Khách");
            }
        }
    }

    private void setupHomeSearch() {
        searchContainer.setOnClickListener(v -> moManHinhSearch());
        edtSearch.setOnClickListener(v -> moManHinhSearch());
        edtSearch.setFocusable(false);
        edtSearch.setCursorVisible(false);
        edtSearch.setKeyListener(null);
    }

    private void moManHinhSearch() {
        startActivity(new Intent(this, SearchActivity.class));
    }

    private void taiSanPhamTrangChu() {
        sanPhamRepository.timKiemSanPham(
                "",
                danhMucDangChon,
                0,
                giaMaxTrangChu,
                0,
                SanPhamRepository.SORT_SP_THEM_VAO_MOI_NHAT,
                new OnFirestoreResult<java.util.List<com.example.hethongbangiay.models.SanPham>>() {
                    @Override
                    public void onSuccess(java.util.List<com.example.hethongbangiay.models.SanPham> data) {
                        sanPhamAdapter.capNhatDuLieu(data);

                        if (danhMucDangChon == null) {
                            tvPopularTitle.setText("Sản phẩm nổi bật");
                        } else {
                            tvPopularTitle.setText("Sản phẩm theo danh mục");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MainActivity.this, "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        xuLyIntentDieuHuong(intent);
    }

    private void moFragment(Fragment fragment) {
        scrollContent.setVisibility(View.GONE);
        fragmentContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void hienTrangChu() {
        fragmentContainer.setVisibility(View.GONE);
        scrollContent.setVisibility(View.VISIBLE);
    }

    private void xuLyIntentDieuHuong(Intent intent) {
        if (intent != null && intent.getBooleanExtra("open_orders", false)) {
            bottomNavigation.setSelectedItemId(R.id.nav_orders);
        } else if (bottomNavigation.getSelectedItemId() == R.id.nav_home) {
            hienTrangChu();
        }
    }
}
