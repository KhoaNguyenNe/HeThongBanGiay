package com.example.hethongbangiay;

import android.app.Application;
import com.google.firebase.FirebaseApp;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Khởi tạo Firebase tại đây
        FirebaseApp.initializeApp(this);
    }
}