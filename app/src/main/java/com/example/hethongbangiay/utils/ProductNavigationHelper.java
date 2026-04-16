package com.example.hethongbangiay.utils;

import android.content.Context;
import android.content.Intent;

import com.example.hethongbangiay.activities.ProductDetailActivity;

public final class ProductNavigationHelper {

    private ProductNavigationHelper() {
    }

    public static void openProductDetail(Context context, String sanPhamId) {
        Intent intent = new Intent(context, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.EXTRA_SAN_PHAM_ID, sanPhamId);
        context.startActivity(intent);
    }
}
