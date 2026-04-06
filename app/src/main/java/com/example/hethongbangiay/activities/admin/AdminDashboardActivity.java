package com.example.hethongbangiay.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;

public class AdminDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        Button btnManageUsers = findViewById(R.id.btnManageUsers);
        Button btnManageProducts = findViewById(R.id.btnManageProducts);
        Button btnManageOrders = findViewById(R.id.btnManageOrders);

        btnManageUsers.setOnClickListener(v ->
                startActivity(new Intent(this, AdminUserManagementActivity.class)));
        // Thêm các nút khác cho sản phẩm, đơn hàng
    }
}