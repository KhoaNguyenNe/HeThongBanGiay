package com.example.hethongbangiay.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.view.Window;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.hethongbangiay.R;

public final class ThemeUtils {

    private ThemeUtils() {
    }

    public static void applySystemBars(Activity activity) {
        Window window = activity.getWindow();
        boolean isNight =
                (activity.getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK)
                        == Configuration.UI_MODE_NIGHT_YES;

        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.app_background));
        window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.app_background));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false);
        }

        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());

        if (controller != null) {
            controller.setAppearanceLightStatusBars(!isNight);
            controller.setAppearanceLightNavigationBars(!isNight);
        }
    }
}

