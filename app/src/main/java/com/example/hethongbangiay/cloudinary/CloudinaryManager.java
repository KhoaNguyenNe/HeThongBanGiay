package com.example.hethongbangiay.cloudinary;

import android.content.Context;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {

    private static boolean initialized = false;

    private CloudinaryManager() {
    }

    public static void init(Context context) {
        if (initialized) {
            return;
        }

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", CloudinaryConfig.CLOUD_NAME);

        MediaManager.init(context, config);
        initialized = true;
    }
}