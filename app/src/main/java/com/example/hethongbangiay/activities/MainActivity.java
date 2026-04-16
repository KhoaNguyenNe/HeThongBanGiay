package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.hethongbangiay.repositories.FavoriteRepository;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.utils.FavoriteUiHelper;
import com.example.hethongbangiay.utils.ImageResolver;
import com.example.hethongbangiay.utils.ProductNavigationHelper;
import com.example.hethongbangiay.utils.ThemeUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseUser;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.repositories.DanhMucRepository;
import com.example.hethongbangiay.repositories.SanPhamRepository;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OPEN_PROFILE = "open_profile";

    // Khai báo repository ở cấp độ lớp để tất cả các hàm đều dùng được
    private NguoiDungRepository repository;
    private TextView tvUsername;
    private TextView tvPopularTitle;
    private ImageView imgAvatar;
    private ImageView ivFavorite;
    private FavoriteRepository favoriteRepository;

    //Biến lấy dữ liệu db của Sp
    private RecyclerView rvProducts;
    private SanPhamAdapter sanPhamAdapter;
    private SanPhamRepository sanPhamRepository;
    //Danh mục
    private RecyclerView rvCategories;
    private DanhMucAdapter danhMucAdapter;
    private DanhMucRepository danhMucRepository;

    //Tìm kiếm
    private CardView searchContainer;
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

        // 1. Khởi tạo Repository và View
        repository = new NguoiDungRepository();
        favoriteRepository = new FavoriteRepository();
        tvUsername = findViewById(R.id.tvUsername);
        tvPopularTitle = findViewById(R.id.tvPopularTitle);
        imgAvatar = findViewById(R.id.imgAvatar);
        ivFavorite = findViewById(R.id.ivFavorite);

        //Lấy dữ liệu Sản phẩm từ db
        rvProducts = findViewById(R.id.rvProducts);
        sanPhamRepository = new SanPhamRepository();
        sanPhamAdapter = new SanPhamAdapter(this, new java.util.ArrayList<>(),
                sp -> ProductNavigationHelper.openProductDetail(MainActivity.this, sp.getSanPhamId()));
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
        setupFavoriteShortcut();

        // --- Cấu hình UI System Bars ---
        ThemeUtils.applySystemBars(this);

        // --- Khởi tạo các View giao diện ---
        View root = findViewById(R.id.main);
        scrollContent = findViewById(R.id.scrollContent);
        fragmentContainer = findViewById(R.id.fragment_container);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        Fragment restoredFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (restoredFragment != null) {
            fragmentContainer.setVisibility(View.VISIBLE);
            scrollContent.setVisibility(View.GONE);
        } else {
            fragmentContainer.setVisibility(View.GONE);
            scrollContent.setVisibility(View.VISIBLE);
        }

        // --- Xử lý Insets (Padding hệ thống cho màn hình tràn viền) ---
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            int bottomNavHeight = bottomNavigation.getHeight();
            if (bottomNavHeight <= 0) {
                bottomNavHeight = (int) (60 * getResources().getDisplayMetrics().density);
            }

            int totalBottomPadding = bottomNavHeight + systemBars.bottom;

            scrollContent.setPadding(systemBars.left, systemBars.top, systemBars.right, totalBottomPadding);

            // Fragment container đã constraint tới top của bottomNavigation,
            // nên không cộng thêm bottomNavHeight nữa để tránh bị hở quá xa.
            fragmentContainer.setPadding(systemBars.left, 0, systemBars.right, 0);

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
                    moFragment(new ProfileFragment());
                    return true;
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
        bottomNavigation.post(this::dongBoNoiDungTheoTabDaChon);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật thông tin người dùng mỗi khi quay lại màn hình chính
        updateUserUI();
        taiDanhSachYeuThich();
        taiSanPhamTrangChu();
    }

    private void updateUserUI() {
        FirebaseUser user = repository.getCurrentUser();

        if (user == null) {
            tvUsername.setText("Khách");
            imgAvatar.setImageResource(R.drawable.avatar);
            return;
        }

        repository.getUserProfile(user.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        tvUsername.setText(user.getEmail() != null ? user.getEmail() : "Khách");
                        imgAvatar.setImageResource(R.drawable.avatar);
                        return;
                    }

                    String hoTen = documentSnapshot.getString("hoTen");
                    String avatar = documentSnapshot.getString("avatar");

                    if (hoTen != null && !hoTen.trim().isEmpty()) {
                        tvUsername.setText(hoTen);
                    } else if (user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
                        tvUsername.setText(user.getDisplayName());
                    } else {
                        tvUsername.setText(user.getEmail());
                    }

                    if (avatar != null && !avatar.trim().isEmpty()) {
                        ImageResolver.loadAvatar(imgAvatar, avatar, true);
                    } else {
                        imgAvatar.setImageResource(R.drawable.avatar);
                    }
                })
                .addOnFailureListener(e -> {
                    tvUsername.setText(user.getEmail() != null ? user.getEmail() : "Khách");
                    imgAvatar.setImageResource(R.drawable.avatar);
                });
    }


    private void setupHomeSearch() {
        searchContainer.setOnClickListener(v -> moManHinhSearch());
        edtSearch.setOnClickListener(v -> moManHinhSearch());
        edtSearch.setFocusable(false);
        edtSearch.setCursorVisible(false);
        edtSearch.setKeyListener(null);
    }

    private void setupFavoriteShortcut() {
        ivFavorite.setOnClickListener(v -> {
            if (!repository.isUserLoggedIn()) {
                Toast.makeText(this, "Vui lòng đăng nhập để xem yêu thích", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                return;
            }

            startActivity(new Intent(MainActivity.this, FavoritesActivity.class));
        });
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

    private void taiDanhSachYeuThich() {
        FavoriteUiHelper.syncFavoriteIds(favoriteRepository, sanPhamAdapter);
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

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return;
        }

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
        } else if (intent != null && intent.getBooleanExtra(EXTRA_OPEN_PROFILE, false)) {
            bottomNavigation.setSelectedItemId(R.id.nav_profile);
        }

        dongBoNoiDungTheoTabDaChon();
    }

    private void dongBoNoiDungTheoTabDaChon() {
        int selectedItemId = bottomNavigation.getSelectedItemId();

        if (selectedItemId == R.id.nav_profile) {
            if (repository.isUserLoggedIn()) {
                moFragment(new ProfileFragment());
            } else {
                bottomNavigation.setSelectedItemId(R.id.nav_home);
            }
            return;
        }

        if (selectedItemId == R.id.nav_cart) {
            moFragment(new CartFragment());
            return;
        }

        if (selectedItemId == R.id.nav_orders) {
            moFragment(new OrdersFragment());
            return;
        }

        if (selectedItemId == R.id.nav_home) {
            hienTrangChu();
        }
    }
}
