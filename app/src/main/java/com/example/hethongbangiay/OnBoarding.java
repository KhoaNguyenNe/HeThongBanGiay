package com.example.hethongbangiay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hethongbangiay.activities.MainActivity;
import com.example.hethongbangiay.adapters.OnBoardingAdapter;
import com.example.hethongbangiay.models.BannerItem;
import com.example.hethongbangiay.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OnBoarding extends AppCompatActivity {
    private final List<BannerItem> items = new ArrayList<>();
    private ViewPager2 viewPageOnBoarding;
    private TextView txtViewSoLuongOnBoard;
    private TextView txtViewSkipOnBoard;
    private Button btnNextOnBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_on_boarding);

        View on_board_main = findViewById(R.id.on_board_main);

        final int padding = on_board_main.getPaddingStart();

        //Lấy các thông tin về các vùng bị thanh hệ thống chiếm chỗ
        ViewCompat.setOnApplyWindowInsetsListener(on_board_main, (v, insets) -> {
            // Lấy kích thước status và navigator bar
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(padding , padding + bars.top, padding, padding + bars.bottom);
            return insets;
        });

        //Thêm nội dung vào OnBoard
        khoiTaoCacBien();
        themBannerItem();
            //Đổ nội dung vào ViewPage
        viewPageOnBoarding.setAdapter(new OnBoardingAdapter(items));
        capNhatSoLuong(0);
        //Sự kiện khi người dùng đổi ViewPager, thì lúc này pos thay đổi -> cần xử lý
        viewPageOnBoarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                capNhatSoLuong(position);
            }
        });
        //Nếu nhấn skip
        txtViewSkipOnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hoanThanhXem();
            }
        });
        //Xem tiếp
        btnNextOnBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos_now = viewPageOnBoarding.getCurrentItem();
                if(pos_now == items.size() - 1) {
                    hoanThanhXem();
                }
                else {
                    viewPageOnBoarding.setCurrentItem(pos_now + 1, true);
                }
            }
        });
    }

    private void khoiTaoCacBien() {
        viewPageOnBoarding = findViewById(R.id.viewPageOnBoard);
        txtViewSkipOnBoard = findViewById(R.id.txtViewSkipOnBoard);
        txtViewSoLuongOnBoard = findViewById(R.id.txtViewSoLuongOnBoard);
        btnNextOnBoard = findViewById(R.id.btnNextOnBoard);
    }


    private void themBannerItem() {
        items.add(new BannerItem("Chất lượng",
                "Chúng tôi cung cấp các sản phẩm chất lượng cao dành riêng cho bạn.",
                "", "quality"));

        items.add(new BannerItem("Sự hài lòng",
                "Sự hài lòng của quý khách là ưu tiên hàng đầu của chúng tôi",
                "", "satisfaction"));

        items.add(new BannerItem("Tham gia ngay",
                "Hày để chúng tôi đáp ứng nhu cầu thời trang của bạn ngay bây giờ",
                "", "tham_gia_ngay"));
    }

    private void capNhatSoLuong(int pos) {
        txtViewSoLuongOnBoard.setText((pos + 1) + " / " + items.size());
        btnNextOnBoard.setText(pos == items.size() - 1 ? "Bắt đầu thôi!" : "Tiếp theo");
    }

    private void hoanThanhXem() {
        new SessionManager(OnBoarding.this).setOnBoardingDone(true);
        Intent diChuyenDenTrangChu = new Intent(OnBoarding.this, MainActivity.class);
        startActivity(diChuyenDenTrangChu);
        finish();
    }
}