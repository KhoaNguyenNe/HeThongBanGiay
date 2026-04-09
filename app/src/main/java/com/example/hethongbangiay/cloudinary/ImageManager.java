package com.example.hethongbangiay.cloudinary;

import android.net.Uri;

import java.util.Map;

public class ImageManager {

    public interface ImageUploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }

    public static void uploadImage(Uri imageUri, String uploadPreset, ImageUploadCallback callback) {
        CloudinaryUploader.uploadImage(imageUri, uploadPreset, new CloudinaryUploader.UploadListener() {
            @Override
            public void onStart(String requestId) {}

            @Override
            public void onProgress(long bytes, long totalBytes) {}

            @Override
            public void onSuccess(Map resultData) {
                String url = (String) resultData.get("secure_url");
                if (callback != null) callback.onSuccess(url);
            }

            @Override
            public void onError(String errorMessage) {
                if (callback != null) callback.onError(errorMessage);
            }

            @Override
            public void onReschedule(String requestId) {}
        });
    }

    // Có thể thêm method delete image nếu Cloudinary hỗ trợ
}