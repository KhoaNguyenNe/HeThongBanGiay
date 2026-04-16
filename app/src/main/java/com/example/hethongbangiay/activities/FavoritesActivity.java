package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.adapters.SanPhamAdapter;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.repositories.FavoriteRepository;
import com.example.hethongbangiay.utils.FavoriteUiHelper;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.utils.ProductNavigationHelper;
import com.example.hethongbangiay.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private RecyclerView rvFavorites;
    private LinearLayout emptyState;
    private SanPhamAdapter sanPhamAdapter;
    private FavoriteRepository favoriteRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_favorites);

        ThemeUtils.applySystemBars(this);

        favoriteRepository = new FavoriteRepository();
        bindViews();
        setupRecyclerView();
        setupInsets();
        setupActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void bindViews() {
        btnBack = findViewById(R.id.btnBack);
        rvFavorites = findViewById(R.id.rvFavorites);
        emptyState = findViewById(R.id.emptyState);
    }

    private void setupRecyclerView() {
        sanPhamAdapter = new SanPhamAdapter(this, new ArrayList<>(),
                sanPham -> ProductNavigationHelper.openProductDetail(FavoritesActivity.this, sanPham.getSanPhamId()));
        rvFavorites.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavorites.setAdapter(sanPhamAdapter);
    }

    private void setupActions() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadFavorites() {
        if (!favoriteRepository.isUserLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem yêu thích", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        favoriteRepository.getCurrentUserFavoriteProducts(new OnFirestoreResult<List<SanPham>>() {
            @Override
            public void onSuccess(List<SanPham> data) {
                sanPhamAdapter.capNhatDuLieu(data);
                FavoriteUiHelper.applyFavoriteProducts(sanPhamAdapter, data);

                boolean isEmpty = data == null || data.isEmpty();
                emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                rvFavorites.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onError(Exception e) {
                sanPhamAdapter.capNhatDuLieu(new ArrayList<>());
                FavoriteUiHelper.applyFavoriteProducts(sanPhamAdapter, new ArrayList<>());
                emptyState.setVisibility(View.VISIBLE);
                rvFavorites.setVisibility(View.GONE);
                Toast.makeText(
                        FavoritesActivity.this,
                        e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "Không tải được danh sách yêu thích",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }

    private void setupInsets() {
        View root = findViewById(R.id.favoritesRoot);
        View topBar = findViewById(R.id.topBar);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            topBar.setPadding(
                    topBar.getPaddingLeft(),
                    systemBars.top + dp(12),
                    topBar.getPaddingRight(),
                    topBar.getPaddingBottom()
            );
            rvFavorites.setPadding(
                    rvFavorites.getPaddingLeft(),
                    rvFavorites.getPaddingTop(),
                    rvFavorites.getPaddingRight(),
                    systemBars.bottom + dp(20)
            );
            return insets;
        });
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
