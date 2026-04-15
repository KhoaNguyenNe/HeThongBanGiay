package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.RoleUtils;
import com.google.firebase.auth.FirebaseAuth;

public class AdminOrderManagementActivity extends AppCompatActivity {

    private final UserRepository userRepository = new UserRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_order_management);

        validatePermission();
    }

    private void validatePermission() {
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
                        Toast.makeText(this, "Lỗi dữ liệu hồ sơ: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    String role = RoleUtils.normalizeRole(profile != null ? profile.getVaiTro() : null);
                    if (!RoleUtils.canManageOrders(role)) {
                        Toast.makeText(this, "Bạn không có quyền quản lý Order", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không xác thực được quyền: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }
}
