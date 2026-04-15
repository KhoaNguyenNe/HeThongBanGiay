package com.example.hethongbangiay.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.ProfileActivity;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.RoleUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_TAB = "extra_target_tab";

    private BottomNavigationView bottomAdminNavigation;
    private TextView tvAdminRole;
    private TextView tvAccessGuide;
    private final UserRepository userRepository = new UserRepository();
    private String currentRole = null;
    private boolean initialTabHandled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        bottomAdminNavigation = findViewById(R.id.bottomAdminNavigation);
        tvAdminRole = findViewById(R.id.tvAdminRole);
        tvAccessGuide = findViewById(R.id.tvAccessGuide);

        initActions();
        loadCurrentRole();
    }

    private void initActions() {
        bottomAdminNavigation.setOnItemSelectedListener(item -> handleBottomNavigation(item.getItemId()));
    }

    private boolean handleBottomNavigation(int itemId) {
        if (itemId == R.id.nav_admin_user) {
            if (!RoleUtils.canManageUsers(currentRole)) {
                Toast.makeText(this, "Bạn không có quyền vào module User", Toast.LENGTH_SHORT).show();
                return false;
            }
            startActivity(new Intent(this, AdminUserManagementActivity.class));
            return true;
        }

        if (itemId == R.id.nav_admin_order) {
            if (!RoleUtils.canManageOrders(currentRole)) {
                Toast.makeText(this, "Bạn không có quyền vào module Order", Toast.LENGTH_SHORT).show();
                return false;
            }
            startActivity(new Intent(this, AdminOrderManagementActivity.class));
            return true;
        }

        if (itemId == R.id.nav_admin_product) {
            if (!RoleUtils.canManageProducts(currentRole)) {
                Toast.makeText(this, "Bạn không có quyền vào module Product", Toast.LENGTH_SHORT).show();
                return false;
            }
            startActivity(new Intent(this, AdminProductManagementActivity.class));
            return true;
        }

        if (itemId == R.id.nav_admin_report) {
            if (!RoleUtils.canViewReports(currentRole)) {
                Toast.makeText(this, "Bạn không có quyền vào module Report", Toast.LENGTH_SHORT).show();
                return false;
            }
            startActivity(new Intent(this, AdminReportActivity.class));
            return true;
        }

        if (itemId == R.id.nav_admin_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        }

        return false;
    }

    private void loadCurrentRole() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    NguoiDung profile;
                    try {
                        profile = documentSnapshot.toObject(NguoiDung.class);
                    } catch (RuntimeException ex) {
                        Toast.makeText(this, "Lỗi đọc hồ sơ quản trị: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }
                    currentRole = RoleUtils.normalizeRole(profile != null ? profile.getVaiTro() : null);

                    if (!RoleUtils.canAccessAdminDashboard(currentRole)) {
                        Toast.makeText(this, "Bạn không có quyền truy cập khu vực quản trị", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    applyRoleNavigationState();
                    handleInitialTabNavigation();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không lấy được vai trò: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void applyRoleNavigationState() {
        boolean canUser = RoleUtils.canManageUsers(currentRole);
        boolean canOrder = RoleUtils.canManageOrders(currentRole);
        boolean canProduct = RoleUtils.canManageProducts(currentRole);
        boolean canReport = RoleUtils.canViewReports(currentRole);

        Menu menu = bottomAdminNavigation.getMenu();
        menu.findItem(R.id.nav_admin_user).setEnabled(canUser);
        menu.findItem(R.id.nav_admin_order).setEnabled(canOrder);
        menu.findItem(R.id.nav_admin_product).setEnabled(canProduct);
        menu.findItem(R.id.nav_admin_report).setEnabled(canReport);
        menu.findItem(R.id.nav_admin_profile).setEnabled(true);

        tvAdminRole.setText("Role: " + currentRole);

        if (RoleUtils.canManageUsers(currentRole)
                && RoleUtils.canManageOrders(currentRole)
                && RoleUtils.canManageProducts(currentRole)) {
            tvAccessGuide.setText("Bạn có toàn quyền. Dùng thanh dưới để vào User, Order, Product, Report hoặc Profile.");
            return;
        }

        if (RoleUtils.canManageUsers(currentRole)) {
            tvAccessGuide.setText("Bạn có quyền quản lý User và xem Report. Các module Order/Product bị khóa.");
            return;
        }

        if (RoleUtils.canManageOrders(currentRole)) {
            tvAccessGuide.setText("Bạn có quyền quản lý Order và xem Report. Các module User/Product bị khóa.");
            return;
        }

        if (RoleUtils.canManageProducts(currentRole)) {
            tvAccessGuide.setText("Bạn có quyền quản lý Product và xem Report. Các module User/Order bị khóa.");
            return;
        }

        tvAccessGuide.setText("Bạn có quyền xem Report và hồ sơ cá nhân.");
    }

    private void handleInitialTabNavigation() {
        if (initialTabHandled) {
            return;
        }

        int targetTab = getIntent().getIntExtra(EXTRA_TARGET_TAB, -1);
        if (targetTab == -1) {
            return;
        }

        initialTabHandled = true;
        getIntent().removeExtra(EXTRA_TARGET_TAB);

        Menu menu = bottomAdminNavigation.getMenu();
        if (menu.findItem(targetTab) == null || !menu.findItem(targetTab).isEnabled()) {
            return;
        }

        bottomAdminNavigation.post(() -> bottomAdminNavigation.setSelectedItemId(targetTab));
    }
}