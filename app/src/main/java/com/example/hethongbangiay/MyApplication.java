package com.example.hethongbangiay;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.hethongbangiay.cloudinary.CloudinaryManager;
import com.example.hethongbangiay.session.SessionManager;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SessionManager session = new SessionManager(this);
        session.setThemeLight(true);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Khởi tạo Firebase tại đây
        FirebaseApp.initializeApp(this);
        //Khởi tạo Cloudinary
        CloudinaryManager.init(this);
    }
}