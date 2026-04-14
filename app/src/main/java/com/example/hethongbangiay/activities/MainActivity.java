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
import androidx.recyclerview.widget.RecyclerView;


import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.adapters.DanhMucAdapter;
import com.example.hethongbangiay.adapters.SanPhamAdapter;
import com.example.hethongbangiay.database.DanhMucDB;
import com.example.hethongbangiay.database.DemoDataSeeder;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.utils.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    // Khai báo repository ở cấp độ lớp để tất cả các hàm đều dùng được
    private NguoiDungRepository repository;
    private TextView tvUsername;
    private TextView tvPopularTitle;

    //Biến lấy dữ liệu db của Sp
    private RecyclerView rvProducts;
    private SanPhamAdapter sanPhamAdapter;
    private SanPhamDB sanPhamDatabase;
    //Danh mục
    private RecyclerView rvCategories;
    private DanhMucAdapter danhMucAdapter;
    private DanhMucDB danhMucDB;

    //Tìm kiếm
    private MaterialCardView searchContainer;
    private EditText edtSearch;
    private String danhMucDangChon = null;
    private double giaMaxTrangChu = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_main);

        new DemoDataSeeder(this).seedIfNeeded();

        // 1. Khởi tạo Repository và View
        repository = new NguoiDungRepository();
        tvUsername = findViewById(R.id.tvUsername);
        tvPopularTitle = findViewById(R.id.tvPopularTitle);

        //Lấy dữ liệu Sản phẩm từ db
        rvProducts = findViewById(R.id.rvProducts);
        sanPhamDatabase = new SanPhamDB(this);
//        sanPhamDatabase.taoDuLieuMau();
        sanPhamAdapter = new SanPhamAdapter(this, sanPhamDatabase.layTatCaSpDangActive());
        rvProducts.setAdapter(sanPhamAdapter);

        //Lấy dữ liệu Danh mục
        rvCategories = findViewById(R.id.rvCategories);
        danhMucDB = new DanhMucDB(this);
        danhMucAdapter = new DanhMucAdapter(this, danhMucDB.layTatCaDMActive(), danhMuc -> {
            if (danhMuc.getDanhMucId().equals(danhMucDangChon)) {
                danhMucDangChon = null;
            } else {
                danhMucDangChon = danhMuc.getDanhMucId();
            }

            danhMucAdapter.setSelectedDanhMucId(danhMucDangChon);
            taiSanPhamTrangChu();
        });
        rvCategories.setAdapter(danhMucAdapter);

        //Khai báo biến tìm kiếm
        searchContainer = findViewById(R.id.searchContainer);
        edtSearch = findViewById(R.id.edtSearch);
        giaMaxTrangChu = sanPhamDatabase.layGiaMax();
        setupHomeSearch();

        // --- Cấu hình UI System Bars ---
        ThemeUtils.applySystemBars(this);

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

            if (id == R.id.nav_orders) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new OrdersFragment())
                        .commit();
            }

            // Thêm các xử lý cho Cart hoặc Home ở đây nếu cần
            return true;
        });

        taiSanPhamTrangChu();
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
                tvUsername.setText("Guest");
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
        sanPhamAdapter.capNhatDuLieu(
                sanPhamDatabase.timKiemSanPham(
                        "",
                        danhMucDangChon,
                        0,
                        giaMaxTrangChu,
                        0,
                        SanPhamDB.SORT_SP_THEM_VAO_MOI_NHAT
                )
        );

        if (danhMucDangChon == null) {
            tvPopularTitle.setText("Most Popular");
        } else {
            tvPopularTitle.setText("Products by category");
        }
    }
}
