package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;
import com.google.android.material.button.MaterialButton;

public class ChooseShippingActivity extends AppCompatActivity {

    public static final String EXTRA_TEN_SHIP = "extra_ten_ship";
    public static final String EXTRA_PHI_SHIP = "extra_phi_ship";

    private CardView cardShippingEconomy;
    private CardView cardShippingRegular;
    private CardView cardShippingCargo;
    private CardView cardShippingExpress;
    private RadioButton rbShippingEconomy;
    private RadioButton rbShippingRegular;
    private RadioButton rbShippingCargo;
    private RadioButton rbShippingExpress;

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
        initEvents();
    }

    private void initViews() {
        cardShippingEconomy = findViewById(R.id.cardShippingEconomy);
        cardShippingRegular = findViewById(R.id.cardShippingRegular);
        cardShippingCargo = findViewById(R.id.cardShippingCargo);
        cardShippingExpress = findViewById(R.id.cardShippingExpress);
        rbShippingEconomy = findViewById(R.id.rbShippingEconomy);
        rbShippingRegular = findViewById(R.id.rbShippingRegular);
        rbShippingCargo = findViewById(R.id.rbShippingCargo);
        rbShippingExpress = findViewById(R.id.rbShippingExpress);

        ImageView btnBackChooseShipping = findViewById(R.id.btnBackChooseShipping);
        MaterialButton btnApplyShipping = findViewById(R.id.btnApplyShipping);

        btnBackChooseShipping.setOnClickListener(v -> finish());
        btnApplyShipping.setOnClickListener(v -> traKetQua());

        rbShippingEconomy.setClickable(false);
        rbShippingEconomy.setFocusable(false);
        rbShippingRegular.setClickable(false);
        rbShippingRegular.setFocusable(false);
        rbShippingCargo.setClickable(false);
        rbShippingCargo.setFocusable(false);
        rbShippingExpress.setClickable(false);
        rbShippingExpress.setFocusable(false);
    }

    private void initState() {
        tenShipDangChon = getIntent().getStringExtra(EXTRA_TEN_SHIP);
        if (tenShipDangChon == null || tenShipDangChon.trim().isEmpty()) {
            tenShipDangChon = "Tiêu chuẩn";
        }

        phiShipDangChon = getIntent().getIntExtra(EXTRA_PHI_SHIP, giaTheoTen(tenShipDangChon));
        capNhatLuaChon(tenShipDangChon, phiShipDangChon);
    }

    private void initEvents() {
        cardShippingEconomy.setOnClickListener(v -> capNhatLuaChon("Tiết kiệm", 10000));
        cardShippingRegular.setOnClickListener(v -> capNhatLuaChon("Tiêu chuẩn", 15000));
        cardShippingCargo.setOnClickListener(v -> capNhatLuaChon("Hàng nặng", 20000));
        cardShippingExpress.setOnClickListener(v -> capNhatLuaChon("Hỏa tốc", 30000));
    }

    private void capNhatLuaChon(String tenShip, int phiShip) {
        tenShipDangChon = tenShip;
        phiShipDangChon = phiShip;

        rbShippingEconomy.setChecked("Tiết kiệm".equals(tenShip));
        rbShippingRegular.setChecked("Tiêu chuẩn".equals(tenShip));
        rbShippingCargo.setChecked("Hàng nặng".equals(tenShip));
        rbShippingExpress.setChecked("Hỏa tốc".equals(tenShip));
    }

    private int giaTheoTen(String tenShip) {
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
        View header = findViewById(R.id.header);
        View bottomBar = findViewById(R.id.bottomBar);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            header.setPadding(
                    header.getPaddingLeft(),
                    dp(18) + bars.top,
                    header.getPaddingRight(),
                    header.getPaddingBottom()
            );

            bottomBar.setPadding(
                    dp(20),
                    bottomBar.getPaddingTop(),
                    dp(20),
                    dp(16) + bars.bottom
            );

            return insets;
        });
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
