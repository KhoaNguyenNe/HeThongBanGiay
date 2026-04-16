package com.example.hethongbangiay.utils;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;

public final class ImageResolver {
    public static String resolveImage(String imgReference) {
        if(imgReference == null) return null;

        String trim = imgReference.trim();
        if(trim.isEmpty()) return null;

        if(trim.startsWith("http://") || trim.startsWith("https://")) return trim;

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

    @DrawableRes
    public static int resolveDrawableResource(Context context, String imgReference) {
        if (imgReference == null) {
            return 0;
        }

        String trim = imgReference.trim();
        if (trim.isEmpty()) {
            return 0;
        }

        return context.getResources().getIdentifier(trim, "drawable", context.getPackageName());
    }

    public static void loadImageReference(ImageView imageView, String imgReference) {
        loadImageReference(imageView, imgReference, R.drawable.shoes);
    }

    public static void loadImageReference(ImageView imageView, String imgReference, @DrawableRes int defaultFallback) {
        Context context = imageView.getContext();
        int drawableResId = resolveDrawableResource(context, imgReference);
        if (drawableResId != 0) {
            imageView.setImageResource(drawableResId);
            return;
        }

        int fallback = TextUtils.isEmpty(imgReference) ? defaultFallback : resolveFallbackDrawable(context, imgReference);
        String imageUrl = resolveImage(imgReference);

        if (imageUrl == null) {
            imageView.setImageResource(fallback);
            return;
        }

        Glide.with(imageView)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(fallback)
                .error(fallback)
                .into(imageView);
    }

    public static void loadAvatar(ImageView imageView, String avatarUrl) {
        loadAvatar(imageView, avatarUrl, false);
    }

    public static void loadAvatar(ImageView imageView, String avatarUrl, boolean skipMemoryCache) {
        String trimmedAvatarUrl = avatarUrl == null ? "" : avatarUrl.trim();
        Object avatarSource = trimmedAvatarUrl.isEmpty() ? R.drawable.avatar : trimmedAvatarUrl;

        Glide.with(imageView)
                .load(avatarSource)
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(skipMemoryCache)
                .circleCrop()
                .into(imageView);
    }
}
