package com.example.hethongbangiay.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.DrawableRes;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;

public final class ImageResolver {
    public static String resolveImage(String imgReference) {
        if(imgReference == null) return null;

        String trim = imgReference.trim();
        if(trim.isEmpty()) return null;

        if(trim.startsWith("https://")) return trim;

        return "https://res.cloudinary.com/"
                + CloudinaryConfig.CLOUD_NAME
                + "/image/upload/"
                + Uri.encode(trim, "/");
    }

    @DrawableRes
    public static int resolveFallbackDrawable(Context context, String imgReference) {
        if(imgReference != null) {
            String trim = imgReference.trim();
            if(!trim.isEmpty()) {
                int id = context.getResources().getIdentifier(trim, "drawable", context.getPackageName());
                if(id != 0)
                    return id;
            }
        }

        return R.drawable.shoes;
    }
}
