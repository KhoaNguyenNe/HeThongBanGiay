package com.example.hethongbangiay.activities;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.DanhGiaAdapter;
import com.example.hethongbangiay.database.DanhGiaDB;
import com.example.hethongbangiay.models.DanhGia;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ProductReviewsActivity extends AppCompatActivity {
    public static final String EXTRA_SAN_PHAM_ID = "extra_san_pham_id";

    private TextView tvReviewSummary;
    private TextView tvEmptyReview;
    private DanhGiaAdapter adapter;
    private final List<DanhGia> allReviews = new ArrayList<>();
    private List<MaterialButton> filterButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_reviews);

        ImageButton btnBack = findViewById(R.id.btnBackReview);
        tvReviewSummary = findViewById(R.id.tvReviewSummary);
        tvEmptyReview = findViewById(R.id.tvEmptyReview);
        RecyclerView rvProductReviews = findViewById(R.id.rvProductReviews);

        MaterialButton btnFilterAll = findViewById(R.id.btnFilterAll);
        MaterialButton btnFilter5 = findViewById(R.id.btnFilter5);
        MaterialButton btnFilter4 = findViewById(R.id.btnFilter4);
        MaterialButton btnFilter3 = findViewById(R.id.btnFilter3);
        MaterialButton btnFilter2 = findViewById(R.id.btnFilter2);
        MaterialButton btnFilter1 = findViewById(R.id.btnFilter1);

        filterButtons = Arrays.asList(
                btnFilterAll, btnFilter5, btnFilter4, btnFilter3, btnFilter2, btnFilter1
        );

        adapter = new DanhGiaAdapter();
        rvProductReviews.setLayoutManager(new LinearLayoutManager(this));
        rvProductReviews.setAdapter(adapter);

        String sanPhamId = getIntent().getStringExtra(EXTRA_SAN_PHAM_ID);

        DanhGiaDB danhGiaDB = new DanhGiaDB(this);
        float diemTB = danhGiaDB.layDiemTrungBinh(sanPhamId);
        int soReview = danhGiaDB.demSoDanhGia(sanPhamId);

        allReviews.clear();
        allReviews.addAll(danhGiaDB.layDanhGiaTheoSanPhamId(sanPhamId));

        tvReviewSummary.setText(String.format(Locale.US, "%.1f (%d đánh giá)", diemTB, soReview));

        btnBack.setOnClickListener(v -> finish());

        btnFilterAll.setOnClickListener(v -> applyFilter(0, btnFilterAll));
        btnFilter5.setOnClickListener(v -> applyFilter(5, btnFilter5));
        btnFilter4.setOnClickListener(v -> applyFilter(4, btnFilter4));
        btnFilter3.setOnClickListener(v -> applyFilter(3, btnFilter3));
        btnFilter2.setOnClickListener(v -> applyFilter(2, btnFilter2));
        btnFilter1.setOnClickListener(v -> applyFilter(1, btnFilter1));

        applyFilter(0, btnFilterAll);
    }

    private void applyFilter(int soSao, MaterialButton activeButton) {
        List<DanhGia> filtered = new ArrayList<>();

        if (soSao == 0) {
            filtered.addAll(allReviews);
        } else {
            for (DanhGia danhGia : allReviews) {
                if (danhGia.getRating() == soSao) {
                    filtered.add(danhGia);
                }
            }
        }

        adapter.submitData(filtered);
        tvEmptyReview.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        updateFilterButtonState(activeButton);
    }

    private void updateFilterButtonState(MaterialButton activeButton) {
        for (MaterialButton button : filterButtons) {
            boolean active = button == activeButton;
            button.setBackgroundTintList(ColorStateList.valueOf(
                    Color.parseColor(active ? "#2A2F3A" : "#181A20")
            ));
            button.setStrokeColor(ColorStateList.valueOf(
                    Color.parseColor(active ? "#2A2F3A" : "#4E535D")
            ));
            button.setTextColor(Color.WHITE);
        }
    }
}
