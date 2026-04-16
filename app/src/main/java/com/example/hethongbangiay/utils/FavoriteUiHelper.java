package com.example.hethongbangiay.utils;

import com.example.hethongbangiay.adapters.SanPhamAdapter;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.repositories.FavoriteRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class FavoriteUiHelper {

    private FavoriteUiHelper() {
    }

    public static void syncFavoriteIds(FavoriteRepository favoriteRepository, SanPhamAdapter sanPhamAdapter) {
        if (sanPhamAdapter == null) {
            return;
        }

        favoriteRepository.getCurrentUserFavoriteProductIds(new OnFirestoreResult<Set<String>>() {
            @Override
            public void onSuccess(Set<String> data) {
                sanPhamAdapter.capNhatYeuThich(data);
            }

            @Override
            public void onError(Exception e) {
                sanPhamAdapter.capNhatYeuThich(Collections.emptySet());
            }
        });
    }

    public static void applyFavoriteProducts(SanPhamAdapter sanPhamAdapter, List<SanPham> sanPhams) {
        Set<String> favoriteIds = new HashSet<>();

        if (sanPhams != null) {
            for (SanPham sanPham : sanPhams) {
                if (sanPham != null && sanPham.getSanPhamId() != null) {
                    favoriteIds.add(sanPham.getSanPhamId());
                }
            }
        }

        sanPhamAdapter.capNhatYeuThich(favoriteIds);
    }
}
