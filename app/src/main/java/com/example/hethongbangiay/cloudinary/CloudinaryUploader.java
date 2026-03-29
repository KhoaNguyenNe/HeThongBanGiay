package com.example.hethongbangiay.cloudinary;

import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

public class CloudinaryUploader {

    public interface UploadListener {
        void onStart(String requestId);
        void onProgress(long bytes, long totalBytes);
        void onSuccess(Map resultData);
        void onError(String errorMessage);
        void onReschedule(String requestId);
    }

    private CloudinaryUploader() {
    }

    public static String uploadImage(Uri fileUri, String uploadPreset, final UploadListener listener) {
        return MediaManager.get()
                .upload(fileUri)
                .unsigned(CloudinaryConfig.UPLOAD_PRESET)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        if (listener != null) listener.onStart(requestId);
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        if (listener != null) listener.onProgress(bytes, totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        if (listener != null) listener.onSuccess(resultData);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        if (listener != null) {
                            listener.onError(error != null ? error.getDescription() : "Upload failed");
                        }
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        if (listener != null) listener.onReschedule(requestId);
                    }
                })
                .dispatch();
    }
}