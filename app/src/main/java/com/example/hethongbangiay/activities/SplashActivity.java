package com.example.hethongbangiay.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;

@SuppressLint("CustomSplashScreen") // Tránh cảnh báo của Android 12+ về Splash mặc định
public class SplashActivity extends AppCompatActivity {

    private static final int DELAY_MILLIS = 2000; // 2 giây hiển thị Logo
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Xử lý Padding cho EdgeToEdge (Tránh logo bị lấp bởi thanh trạng thái/điều hướng)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Định nghĩa hành động chuyển màn hình
        runnable = () -> {
            // LUỒNG: Vào thẳng MainActivity để khách xem giày tự do
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            // Hiệu ứng chuyển cảnh mượt mà (Tùy chọn)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        };

        // Bắt đầu đếm ngược
        handler.postDelayed(runnable, DELAY_MILLIS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // QUAN TRỌNG: Hủy callback nếu user thoát app trước khi hết 2 giây để tránh rò rỉ bộ nhớ (Memory Leak)
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }
}