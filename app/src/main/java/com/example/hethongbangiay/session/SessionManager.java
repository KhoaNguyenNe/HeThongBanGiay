package com.example.hethongbangiay.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {

    private static final String Session_name = "Shoea_Session";
    private static final String KEY_ONBOARDING = "onboarding_done";
    private static final String KEY_THEME_MODE = "theme_mode";
    private static final Boolean THEME_LIGHT = true;
    private static final Boolean THEME_DARK = false;

    private final SharedPreferences preferences;
    public SessionManager(Context context) {
        preferences = context.getSharedPreferences(Session_name, Context.MODE_PRIVATE);
    }

    public void setOnBoardingDone(Boolean done) {
        preferences.edit().putBoolean(KEY_ONBOARDING, done).apply();
    }

    public boolean isOnBoardingDone() {
        return preferences.getBoolean(KEY_ONBOARDING, false);
    }

    public void setThemeLight(Boolean isLight) {
        preferences.edit().putBoolean(KEY_THEME_MODE, isLight);
    }

    public boolean getThemeLight() {
        return preferences.getBoolean(KEY_THEME_MODE, THEME_LIGHT);
    }

}
