package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.UserRepository;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminUserManagementActivity extends AppCompatActivity {

    private ListView lvUsers;
    private UserRepository userRepository;
    private List<NguoiDung> userList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_management);
    }

    private void loadUsers() {
        userRepository.getAllUsers().addOnSuccessListener(querySnapshot -> {
            userList.clear();
            List<String> displayList = new ArrayList<>();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                NguoiDung user = doc.toObject(NguoiDung.class);
                userList.add(user);
                displayList.add(user.getEmail() + " - " + user.getVaiTro());
            }
            adapter.clear();
            adapter.addAll(displayList);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải danh sách user", Toast.LENGTH_SHORT).show();
        });
    }

    private void showUserActionDialog(NguoiDung user) {
        // Sử dụng AlertDialog để chọn action: cập nhật role, khóa tài khoản
        // Ví dụ: cập nhật role thành ADMIN hoặc USER
        userRepository.updateUserRole(user.getUid(), "ADMIN").addOnSuccessListener(aVoid -> {
            Toast.makeText(this, "Đã cập nhật role", Toast.LENGTH_SHORT).show();
            loadUsers();
        });
    }
}