package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.database.DanhGiaDB;
import com.example.hethongbangiay.database.GioHangDB;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.database.SizeGiayDB;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.models.SizeGiay;
import com.example.hethongbangiay.utils.ImageResolver;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductDetailActivity extends AppCompatActivity {
    public static final String EXTRA_SAN_PHAM_ID = "extra_san_pham_id";

    private ImageButton btnBack;
    private ImageView imgProduct;
    private TextView tvProductName, tvRatingInfo, tvDescription, tvStockInfo, tvQuantity, tvTotalPrice, tvSoldInfo;
    private MaterialButton btnMinus, btnPlus, btnViewReviews, btnAddToCart;
    private LinearLayout layoutSizes;

    private SanPham sanPham;
    private SizeGiay sizeDangChon;
    private int soLuongChon = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        applyInsets();

        initViews();
        loadData();
        initEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgProduct = findViewById(R.id.imgProductDetail);
        tvProductName = findViewById(R.id.tvProductNameDetail);
        tvRatingInfo = findViewById(R.id.tvRatingInfo);
        tvDescription = findViewById(R.id.tvDescriptionDetail);
        tvStockInfo = findViewById(R.id.tvStockInfo);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        btnViewReviews = findViewById(R.id.btnViewReviews);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        layoutSizes = findViewById(R.id.layoutSizes);
        tvSoldInfo = findViewById(R.id.tvSoldInfo);
    }

    private void loadData() {
        String sanPhamId = getIntent().getStringExtra(EXTRA_SAN_PHAM_ID);
        sanPham = new SanPhamDB(this).timKiemSpTheoId(sanPhamId);

        if (sanPham == null) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvProductName.setText(sanPham.getTenSanPham());
        tvDescription.setText(sanPham.getMoTaSanPham());

        // Glide là thư viện ngoài, nhưng project của bạn đã có sẵn dependency trong build.gradle.kts.
        String imgUrl = ImageResolver.resolveImage(sanPham.getAnhSanPham());
        int fallback = ImageResolver.resolveFallbackDrawable(this, sanPham.getAnhSanPham());
        if (imgUrl == null) {
            imgProduct.setImageResource(fallback);
        } else {
            Glide.with(this).load(imgUrl).placeholder(fallback).error(fallback).into(imgProduct);
        }

        DanhGiaDB danhGiaDB = new DanhGiaDB(this);
        float diemTB = danhGiaDB.layDiemTrungBinh(sanPham.getSanPhamId());
        if (diemTB == 0f && sanPham.getDiemDanhGia() > 0) {
            diemTB = (float) sanPham.getDiemDanhGia();
        }
        int soReview = danhGiaDB.demSoDanhGia(sanPham.getSanPhamId());

        tvSoldInfo.setText(String.format(Locale.US, "%,d sold", sanPham.getLuotBan()));
        tvRatingInfo.setText(String.format(Locale.US, "%.1f (%d reviews)", diemTB, soReview));

        renderSizes(new SizeGiayDB(this).laySizeTheoSanPhamId(sanPham.getSanPhamId()));
        capNhatTongTien();
    }

    private void initEvents() {
        btnBack.setOnClickListener(v -> finish());

        btnMinus.setOnClickListener(v -> {
            if (soLuongChon > 1) {
                soLuongChon--;
                capNhatTongTien();
            }
        });

        btnPlus.setOnClickListener(v -> {
            if (sizeDangChon == null) {
                Toast.makeText(this, "Bạn hãy chọn size trước", Toast.LENGTH_SHORT).show();
                return;
            }

            if (soLuongChon >= sizeDangChon.getSoLuong()) {
                Toast.makeText(this, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show();
                return;
            }

            soLuongChon++;
            capNhatTongTien();
        });

        btnViewReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductReviewsActivity.class);
            intent.putExtra(ProductReviewsActivity.EXTRA_SAN_PHAM_ID, sanPham.getSanPhamId());
            startActivity(intent);
        });

        btnAddToCart.setOnClickListener(v -> {
            if (sizeDangChon == null) {
                Toast.makeText(this, "Bạn chưa chọn size", Toast.LENGTH_SHORT).show();
                return;
            }

            new GioHangDB(this).themSanPhamVaoGio(sanPham, sizeDangChon, soLuongChon);
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
    }

    private void renderSizes(List<SizeGiay> dsSize) {
        layoutSizes.removeAllViews();
        sizeDangChon = null;

        if (dsSize == null || dsSize.isEmpty()) {
            tvStockInfo.setText("Sản phẩm chưa có size");
            btnAddToCart.setEnabled(false);
            return;
        }

        for (SizeGiay item : dsSize) {
            TextView tvSize = taoViewSize(item);
            layoutSizes.addView(tvSize);

            if (sizeDangChon == null && item.getSoLuong() > 0) {
                chonSize(item, tvSize);
            }
        }
    }

    private TextView taoViewSize(SizeGiay item) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(52), dp(52));
        params.setMargins(0, 0, dp(12), 0);
        tv.setLayoutParams(params);
        tv.setGravity(Gravity.CENTER);
        tv.setText(String.valueOf(item.getSize()));
        tv.setTextSize(16);
        tv.setTextColor(Color.WHITE);
        tv.setBackground(taoNenSize(false));

        if (item.getSoLuong() <= 0) {
            tv.setAlpha(0.35f);
        }

        tv.setOnClickListener(v -> {
            if (item.getSoLuong() <= 0) {
                Toast.makeText(this, "Size này đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            chonSize(item, tv);
        });

        return tv;
    }

    private void chonSize(SizeGiay sizeGiay, TextView selectedView) {
        sizeDangChon = sizeGiay;
        soLuongChon = 1;

        for (int i = 0; i < layoutSizes.getChildCount(); i++) {
            TextView child = (TextView) layoutSizes.getChildAt(i);
            boolean isSelected = child == selectedView;
            child.setBackground(taoNenSize(isSelected));
            child.setTextColor(isSelected ? Color.parseColor("#181A20") : Color.WHITE);
        }

        tvStockInfo.setText("Còn " + sizeGiay.getSoLuong() + " sản phẩm");
        capNhatTongTien();
    }

    private GradientDrawable taoNenSize(boolean selected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.parseColor(selected ? "#FFFFFF" : "#2A2F3A"));
        drawable.setStroke(dp(1), Color.parseColor(selected ? "#FFFFFF" : "#4A5060"));
        return drawable;
    }

    private void capNhatTongTien() {
        tvQuantity.setText(String.valueOf(soLuongChon));

        if (sanPham == null) return;

        double tongTien = sanPham.getDonGia() * soLuongChon;
        NumberFormat format = NumberFormat.getInstance(new Locale("vi", "VN"));
        tvTotalPrice.setText(format.format(tongTien) + " đ");
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void applyInsets() {
        View root = findViewById(R.id.detailRoot);
        View topBar = findViewById(R.id.topBar);
        View bottomBar = findViewById(R.id.bottomActionBar);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            topBar.setPadding(
                    dp(14),
                    bars.top + dp(8),
                    dp(14),
                    dp(8)
            );

            bottomBar.setPadding(
                    dp(22),
                    dp(14),
                    dp(22),
                    bars.bottom + dp(14)
            );

            return insets;
        });
    }

}
