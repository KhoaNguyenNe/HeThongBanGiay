package com.example.hethongbangiay.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.DanhGiaAdapter;
import com.example.hethongbangiay.models.DanhGia;
import com.example.hethongbangiay.repositories.DanhGiaRepository;
import com.example.hethongbangiay.utils.ThemeUtils;
import com.example.hethongbangiay.utils.OnFirestoreResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ProductReviewsActivity extends AppCompatActivity {
    public static final String EXTRA_SAN_PHAM_ID = "extra_san_pham_id";

    private TextView tvReviewSummary;
    private TextView tvEmptyReview;
    private DanhGiaAdapter adapter;
    private final List<DanhGia> allReviews = new ArrayList<>();
    private List<AppCompatButton> filterButtons;
    private final DanhGiaRepository danhGiaRepository = new DanhGiaRepository();
    private String sanPhamId;
    private int currentFilterStars = 0;
    private AppCompatButton currentFilterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_product_reviews);
        ThemeUtils.applySystemBars(this);

        ImageView btnBack = findViewById(R.id.btnBackReview);
        tvReviewSummary = findViewById(R.id.tvReviewSummary);
        tvEmptyReview = findViewById(R.id.tvEmptyReview);
        RecyclerView rvProductReviews = findViewById(R.id.rvProductReviews);

        AppCompatButton btnFilterAll = findViewById(R.id.btnFilterAll);
        AppCompatButton btnFilter5 = findViewById(R.id.btnFilter5);
        AppCompatButton btnFilter4 = findViewById(R.id.btnFilter4);
        AppCompatButton btnFilter3 = findViewById(R.id.btnFilter3);
        AppCompatButton btnFilter2 = findViewById(R.id.btnFilter2);
        AppCompatButton btnFilter1 = findViewById(R.id.btnFilter1);

        filterButtons = Arrays.asList(
                btnFilterAll, btnFilter5, btnFilter4, btnFilter3, btnFilter2, btnFilter1
        );

        applyInsets();

        adapter = new DanhGiaAdapter();
        rvProductReviews.setLayoutManager(new LinearLayoutManager(this));
        rvProductReviews.setAdapter(adapter);

        sanPhamId = getIntent().getStringExtra(EXTRA_SAN_PHAM_ID);
        currentFilterButton = btnFilterAll;

        btnBack.setOnClickListener(v -> finish());

        btnFilterAll.setOnClickListener(v -> applyFilter(0, btnFilterAll));
        btnFilter5.setOnClickListener(v -> applyFilter(5, btnFilter5));
        btnFilter4.setOnClickListener(v -> applyFilter(4, btnFilter4));
        btnFilter3.setOnClickListener(v -> applyFilter(3, btnFilter3));
        btnFilter2.setOnClickListener(v -> applyFilter(2, btnFilter2));
        btnFilter1.setOnClickListener(v -> applyFilter(1, btnFilter1));

        applyFilter(0, btnFilterAll);
        taiDanhGia();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDanhGia();
    }

    private void taiDanhGia() {
        if (sanPhamId == null || sanPhamId.trim().isEmpty()) {
            tvReviewSummary.setText("0.0 (0 đánh giá)");
            allReviews.clear();
            adapter.submitData(new ArrayList<>());
            tvEmptyReview.setVisibility(View.VISIBLE);
            return;
        }

        danhGiaRepository.layDanhGiaTheoSanPhamId(sanPhamId, new OnFirestoreResult<List<DanhGia>>() {
            @Override
            public void onSuccess(List<DanhGia> data) {
                allReviews.clear();
                if (data != null) {
                    allReviews.addAll(data);
                }

                allReviews.sort((a, b) -> {
                    if (a == null && b == null) return 0;
                    if (a == null) return 1;
                    if (b == null) return -1;
                    if (a.getNgayDanhGia() == null && b.getNgayDanhGia() == null) return 0;
                    if (a.getNgayDanhGia() == null) return 1;
                    if (b.getNgayDanhGia() == null) return -1;
                    return b.getNgayDanhGia().compareTo(a.getNgayDanhGia());
                });

                int count = allReviews.size();
                float avg = 0f;
                if (count > 0) {
                    int sum = 0;
                    for (DanhGia dg : allReviews) {
                        if (dg != null) sum += dg.getRating();
                    }
                    avg = (float) sum / (float) count;
                }

                tvReviewSummary.setText(String.format(Locale.US, "%.1f (%d đánh giá)", avg, count));

                // Re-apply current filter after reload
                applyFilter(currentFilterStars, currentFilterButton == null ? filterButtons.get(0) : currentFilterButton);
            }

            @Override
            public void onError(Exception e) {
                tvReviewSummary.setText("0.0 (0 đánh giá)");
                allReviews.clear();
                adapter.submitData(new ArrayList<>());
                tvEmptyReview.setVisibility(View.VISIBLE);
            }
        });
    }

    private void applyFilter(int soSao, AppCompatButton activeButton) {
        currentFilterStars = soSao;
        currentFilterButton = activeButton;

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

    private void updateFilterButtonState(AppCompatButton activeButton) {
        for (AppCompatButton button : filterButtons) {
            boolean active = button == activeButton;
            button.setBackgroundResource(active
                    ? R.drawable.bg_button_filter_chip_selected
                    : R.drawable.bg_button_filter_chip);
            button.setTextColor(getColor(active ? R.color.button_primary_text : R.color.app_text_primary));
        }
    }

    private void applyInsets() {
        View root = findViewById(R.id.reviewRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + 16, bars.top + 18, bars.right + 16, bars.bottom + 8);
            return insets;
        });
    }
}
