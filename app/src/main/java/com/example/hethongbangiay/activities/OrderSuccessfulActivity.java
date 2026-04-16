package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;
import androidx.appcompat.widget.AppCompatButton;

public class OrderSuccessfulActivity extends AppCompatActivity {

    public static final String EXTRA_DON_HANG_ID = "extra_don_hang_id";
    public static final String EXTRA_HOA_DON_ID = "extra_hoa_don_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.dialog_order_success);

        AppCompatButton btnViewOrder = findViewById(R.id.btnViewOrder);
        AppCompatButton btnViewReceipt = findViewById(R.id.btnViewReceipt);

        applyInsets();

        btnViewOrder.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("open_orders", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        btnViewReceipt.setOnClickListener(v -> {
            String hoaDonId = getIntent().getStringExtra(EXTRA_HOA_DON_ID);
            Toast.makeText(this, "HoaDon đã lưu trên Firebase: " + hoaDonId, Toast.LENGTH_LONG).show();
        });
    }

    private void applyInsets() {
        View root = findViewById(android.R.id.content);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
