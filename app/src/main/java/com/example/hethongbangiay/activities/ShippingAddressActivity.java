package com.example.hethongbangiay.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.DiaChiAdapter;
import com.example.hethongbangiay.models.DiaChi;
import com.example.hethongbangiay.repositories.DiaChiRepository;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.session.SessionManager;
import androidx.appcompat.widget.AppCompatButton;

import java.util.List;

public class ShippingAddressActivity extends AppCompatActivity {

    public static final String EXTRA_DIA_CHI_DA_CHON = "extra_dia_chi_da_chon";

    private ImageView btnBackAddress;
    private ImageView btnAddAddress;
    private RecyclerView rvAddress;
    private AppCompatButton btnApplyAddress;

    private DiaChiAdapter diaChiAdapter;
    private DiaChiRepository diaChiRepository;
    private NguoiDungRepository nguoiDungRepository;
    private SessionManager sessionManager;
    private DiaChi diaChiDangChon;

    private final ActivityResultLauncher<Intent> formDiaChiLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    taiDanhSachDiaChi();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_shipping_address);

        initViews();
        initObjects();
        applyInsets();

        if (!nguoiDungRepository.isUserLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để chọn địa chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupRecyclerView();
        initEvents();
        taiDanhSachDiaChi();
    }

    private void initViews() {
        btnBackAddress = findViewById(R.id.btnBackAddress);
        btnAddAddress = findViewById(R.id.btnAddAddress);
        rvAddress = findViewById(R.id.rvAddress);
        btnApplyAddress = findViewById(R.id.btnApplyAddress);
    }

    private void initObjects() {
        diaChiRepository = new DiaChiRepository();
        nguoiDungRepository = new NguoiDungRepository();
        sessionManager = new SessionManager(this);
        diaChiAdapter = new DiaChiAdapter(new DiaChiAdapter.OnDiaChiSelectedListener() {
            @Override
            public void onDiaChiSelected(DiaChi diaChi) {
                diaChiDangChon = diaChi;
            }

            @Override
            public void onSuaDiaChi(DiaChi diaChi) {
                Intent intent = new Intent(ShippingAddressActivity.this, AddressFormActivity.class);
                intent.putExtra(AddressFormActivity.EXTRA_DIA_CHI, diaChi);
                formDiaChiLauncher.launch(intent);
            }

            @Override
            public void onXoaDiaChi(DiaChi diaChi) {
                hoiXoaDiaChi(diaChi);
            }
        });
    }

    private void setupRecyclerView() {
        rvAddress.setLayoutManager(new LinearLayoutManager(this));
        rvAddress.setAdapter(diaChiAdapter);
    }

    private void initEvents() {
        btnBackAddress.setOnClickListener(v -> finish());
        btnAddAddress.setOnClickListener(v -> formDiaChiLauncher.launch(
                new Intent(this, AddressFormActivity.class)
        ));

        btnApplyAddress.setOnClickListener(v -> {
            if (diaChiDangChon == null) {
                Toast.makeText(this, "Bạn hãy chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.setDiaChiCheckout(diaChiDangChon.getDiaChiId());

            Intent data = new Intent();
            data.putExtra(EXTRA_DIA_CHI_DA_CHON, diaChiDangChon);
            setResult(RESULT_OK, data);
            finish();
        });
    }

    private void taiDanhSachDiaChi() {
        String diaChiDangChonId = sessionManager.getDiaChiCheckout();

        diaChiRepository.layDiaChiTheoNguoiDung(nguoiDungRepository.getCurrentUser().getUid(), new DiaChiRepository.DiaChiListCallback() {
            @Override
            public void onSuccess(List<DiaChi> dsDiaChi) {
                if (dsDiaChi.isEmpty()) {
                    diaChiDangChon = null;
                    sessionManager.setDiaChiCheckout("");
                    Toast.makeText(ShippingAddressActivity.this, "Bạn chưa có địa chỉ trong Firebase", Toast.LENGTH_SHORT).show();
                } else if (diaChiDangChon == null) {
                    for (DiaChi item : dsDiaChi) {
                        if (item.getDiaChiId().equals(diaChiDangChonId)) {
                            diaChiDangChon = item;
                            break;
                        }
                    }

                    if (diaChiDangChon == null) {
                        diaChiDangChon = dsDiaChi.get(0);
                    }
                } else {
                    boolean timThay = false;
                    for (DiaChi item : dsDiaChi) {
                        if (item.getDiaChiId().equals(diaChiDangChon.getDiaChiId())) {
                            diaChiDangChon = item;
                            timThay = true;
                            break;
                        }
                    }

                    if (!timThay) {
                        diaChiDangChon = dsDiaChi.get(0);
                    }
                }

                diaChiAdapter.capNhatDuLieu(dsDiaChi, diaChiDangChon != null ? diaChiDangChon.getDiaChiId() : diaChiDangChonId);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(ShippingAddressActivity.this, "Không tải được địa chỉ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyInsets() {
        View root = findViewById(R.id.addressRoot);
        View header = findViewById(R.id.header);
        View bottomBar = findViewById(R.id.bottomBar);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            header.setPadding(
                    header.getPaddingLeft(),
                    dp(18) + bars.top,
                    header.getPaddingRight(),
                    header.getPaddingBottom()
            );

            bottomBar.setPadding(
                    dp(20),
                    bottomBar.getPaddingTop(),
                    dp(20),
                    dp(16) + bars.bottom
            );

            return insets;
        });
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private void hoiXoaDiaChi(DiaChi diaChi) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    diaChiRepository.xoaDiaChi(nguoiDungRepository.getCurrentUser().getUid(), diaChi.getDiaChiId(), new DiaChiRepository.ActionCallback() {
                        @Override
                        public void onSuccess() {
                            if (diaChiDangChon != null && diaChi.getDiaChiId().equals(diaChiDangChon.getDiaChiId())) {
                                diaChiDangChon = null;
                                sessionManager.setDiaChiCheckout("");
                            }
                            Toast.makeText(ShippingAddressActivity.this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                            taiDanhSachDiaChi();
                        }

                        @Override
                        public void onError(Exception e) {
                            Toast.makeText(ShippingAddressActivity.this, "Không xóa được địa chỉ", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
