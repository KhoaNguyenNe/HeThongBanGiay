package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.ShippingOptionAdapter;
import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

public class ChooseShippingActivity extends AppCompatActivity {

    public static final String EXTRA_TEN_SHIP = "extra_ten_ship";
    public static final String EXTRA_PHI_SHIP = "extra_phi_ship";

    private RecyclerView rvShippingOptions;
    private ShippingOptionAdapter shippingOptionAdapter;
    private final List<ShippingOptionAdapter.ShippingOption> dsShipping = new ArrayList<>();

    private String tenShipDangChon = "Tiêu chuẩn";
    private int phiShipDangChon = 15000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_choose_shipping);

        initViews();
        applyInsets();
        initState();
    }

    private void initViews() {
        rvShippingOptions = findViewById(R.id.rvShippingOptions);

        ImageView btnBackChooseShipping = findViewById(R.id.btnBackChooseShipping);
        AppCompatButton btnApplyShipping = findViewById(R.id.btnApplyShipping);

        btnBackChooseShipping.setOnClickListener(v -> finish());
        btnApplyShipping.setOnClickListener(v -> traKetQua());

        shippingOptionAdapter = new ShippingOptionAdapter(option -> capNhatLuaChon(option.getTen(), option.getGia()));
        rvShippingOptions.setLayoutManager(new LinearLayoutManager(this));
        rvShippingOptions.setAdapter(shippingOptionAdapter);

        dsShipping.clear();
        dsShipping.add(new ShippingOptionAdapter.ShippingOption("Tiết kiệm", "Dự kiến nhận sau 5 ngày", 10000, R.drawable.ic_shipping));
        dsShipping.add(new ShippingOptionAdapter.ShippingOption("Tiêu chuẩn", "Dự kiến nhận sau 4 ngày", 15000, R.drawable.ic_shipping));
        dsShipping.add(new ShippingOptionAdapter.ShippingOption("Hàng nặng", "Dự kiến nhận sau 4 ngày", 20000, R.drawable.ic_shipping));
        dsShipping.add(new ShippingOptionAdapter.ShippingOption("Hỏa tốc", "Dự kiến nhận sau 2 ngày", 30000, R.drawable.ic_shipping));
    }

    private void initState() {
        tenShipDangChon = getIntent().getStringExtra(EXTRA_TEN_SHIP);
        if (tenShipDangChon == null || tenShipDangChon.trim().isEmpty()) {
            tenShipDangChon = "Tiêu chuẩn";
        }

        phiShipDangChon = getIntent().getIntExtra(EXTRA_PHI_SHIP, giaTheoTen(tenShipDangChon));
        capNhatLuaChon(tenShipDangChon, phiShipDangChon);
    }

    private void capNhatLuaChon(String tenShip, int phiShip) {
        tenShipDangChon = tenShip;
        phiShipDangChon = phiShip;
        shippingOptionAdapter.capNhatDuLieu(dsShipping, tenShipDangChon);
    }

    private int giaTheoTen(String tenShip) {
        if (tenShip == null) return 15000;
        switch (tenShip) {
            case "Tiết kiệm":
                return 10000;
            case "Hàng nặng":
                return 20000;
            case "Hỏa tốc":
                return 30000;
            default:
                return 15000;
        }
    }

    private void traKetQua() {
        Intent data = new Intent();
        data.putExtra(EXTRA_TEN_SHIP, tenShipDangChon);
        data.putExtra(EXTRA_PHI_SHIP, phiShipDangChon);
        setResult(RESULT_OK, data);
        finish();
    }

    private void applyInsets() {
        View root = findViewById(R.id.chooseShippingRoot);
        if (root == null) return;
        
        View header = findViewById(R.id.header);
        View bottomBar = findViewById(R.id.bottomBar);
        
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            if (header != null) {
                header.setPadding(
                        header.getPaddingLeft(),
                        dp(18) + bars.top,
                        header.getPaddingRight(),
                        header.getPaddingBottom()
                );
            }

            if (bottomBar != null) {
                bottomBar.setPadding(
                        dp(20),
                        bottomBar.getPaddingTop(),
                        dp(20),
                        dp(16) + bars.bottom
                );
            }

            return insets;
        });
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
