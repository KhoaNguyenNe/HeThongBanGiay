package com.example.hethongbangiay.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.activities.auth.RegisterActivity;
import com.example.hethongbangiay.adapters.CheckoutAdapter;
import com.example.hethongbangiay.database.GioHangDB;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DiaChi;
import com.example.hethongbangiay.repositories.DiaChiRepository;
import com.example.hethongbangiay.session.SessionManager;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.utils.FormatUtils;
import androidx.appcompat.widget.AppCompatButton;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView btnBackCheckout;
    private View cardCompactAddress;
    private ImageView btnEditAddress;
    private TextView tvAddressType;
    private TextView tvAddressValue;
    private View cardShippingChoice;
    private TextView tvShippingChoice;
    private RecyclerView rvCheckoutItems;
    private TextView tvAmountValue;
    private TextView tvShippingValue;
    private TextView tvTotalValue;
    private AppCompatButton btnContinuePayment;

    private GioHangDB gioHangDB;
    private SessionManager sessionManager;
    private NguoiDungRepository nguoiDungRepository;
    private DiaChiRepository diaChiRepository;
    private CheckoutAdapter checkoutAdapter;
    private DiaChi diaChiDangChon;
    private int tongTienHang = 0;
    private int phiShip = 15000;
    private String tenShip = "Tiêu chuẩn";

    private final ActivityResultLauncher<Intent> chonDiaChiLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    DiaChi diaChi = (DiaChi) result.getData().getSerializableExtra(ShippingAddressActivity.EXTRA_DIA_CHI_DA_CHON);
                    if (diaChi != null) {
                        diaChiDangChon = diaChi;
                        sessionManager.setDiaChiCheckout(diaChiDangChon.getDiaChiId());
                        hienThiDiaChi(diaChiDangChon);
                    }
                }
            });

    private final ActivityResultLauncher<Intent> chonShippingLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    tenShip = result.getData().getStringExtra(ChooseShippingActivity.EXTRA_TEN_SHIP);
                    phiShip = result.getData().getIntExtra(ChooseShippingActivity.EXTRA_PHI_SHIP, 15000);
                    if (tenShip == null || tenShip.trim().isEmpty()) {
                        tenShip = "Tiêu chuẩn";
                    }
                    sessionManager.setTenShip(tenShip);
                    sessionManager.setPhiShip(phiShip);
                    capNhatHienThiShip();
                    capNhatThongTinTien();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_checkout);

        initViews();
        initObjects();
        setupRecyclerView();
        initEvents();
        applyInsets();
        taiDanhSachSanPham();
        taiDiaChiHienTai();
        capNhatThongTinTien();
    }

    @Override
    protected void onResume() {
        super.onResume();
        taiDiaChiHienTai();
        taiDanhSachSanPham();
        capNhatThongTinTien();
    }

    private void initViews() {
        btnBackCheckout = findViewById(R.id.btnBackCheckout);
        cardCompactAddress = findViewById(R.id.cardCompactAddress);
        btnEditAddress = findViewById(R.id.btnEditAddress);
        tvAddressType = findViewById(R.id.tvAddressType);
        tvAddressValue = findViewById(R.id.tvAddressValue);
        cardShippingChoice = findViewById(R.id.cardShippingChoice);
        tvShippingChoice = findViewById(R.id.tvShippingChoice);
        rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        tvAmountValue = findViewById(R.id.tvAmountValue);
        tvShippingValue = findViewById(R.id.tvShippingValue);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        btnContinuePayment = findViewById(R.id.btnContinuePayment);
    }

    private void initObjects() {
        gioHangDB = new GioHangDB(this);
        sessionManager = new SessionManager(this);
        nguoiDungRepository = new NguoiDungRepository();
        diaChiRepository = new DiaChiRepository();
        checkoutAdapter = new CheckoutAdapter();
        tenShip = sessionManager.getTenShip();
        phiShip = sessionManager.getPhiShip();
        capNhatHienThiShip();
    }

    private void setupRecyclerView() {
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        rvCheckoutItems.setAdapter(checkoutAdapter);
    }

    private void initEvents() {
        btnBackCheckout.setOnClickListener(v -> finish());

        View.OnClickListener moChonDiaChi = v -> {
            if (!nguoiDungRepository.isUserLoggedIn()) {
                Toast.makeText(this, "Bạn sẽ chọn địa chỉ sau khi đăng nhập thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(this, ShippingAddressActivity.class);
            chonDiaChiLauncher.launch(intent);
        };

        cardCompactAddress.setOnClickListener(moChonDiaChi);
        btnEditAddress.setOnClickListener(moChonDiaChi);
        cardShippingChoice.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChooseShippingActivity.class);
            intent.putExtra(ChooseShippingActivity.EXTRA_TEN_SHIP, tenShip);
            intent.putExtra(ChooseShippingActivity.EXTRA_PHI_SHIP, phiShip);
            chonShippingLauncher.launch(intent);
        });

        btnContinuePayment.setOnClickListener(v -> {
            if (gioHangDB.gioHangTrong()) {
                Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!nguoiDungRepository.isUserLoggedIn()) {
                showAuthSelectionDialog();
            } else {
                tiepTucThanhToan();
            }
        });
    }

    private void showAuthSelectionDialog() {
        sessionManager.batDauChoXuLyThanhToan();

        new AlertDialog.Builder(this)
                .setTitle("Xác nhận tài khoản")
                .setMessage("Bạn cần đăng nhập để tiếp tục thanh toán. Bạn đã có tài khoản chưa?")
                .setPositiveButton("Đã có", (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                })
                .setNegativeButton("Chưa có", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                })
                .setNeutralButton("Để sau", null)
                .show();
    }

    private void tiepTucThanhToan() {
        sessionManager.batDauChoXuLyThanhToan();
        sessionManager.setPhiShip(phiShip);
        sessionManager.setTenShip(tenShip);
        if (diaChiDangChon != null) {
            sessionManager.setDiaChiCheckout(diaChiDangChon.getDiaChiId());
        }

        startActivity(new Intent(this, PaymentMethodActivity.class));
    }

    private void taiDanhSachSanPham() {
        List<ChiTietDonHang> dsSanPham = gioHangDB.layTatCaSanPhamTrongGio();
        checkoutAdapter.capNhatDuLieu(dsSanPham);
        tongTienHang = gioHangDB.tongTienGioHang();
    }

    private void taiDiaChiHienTai() {
        if (!nguoiDungRepository.isUserLoggedIn()) {
            diaChiDangChon = null;
            tvAddressType.setText("Chưa đăng nhập");
            tvAddressValue.setText("Địa chỉ giao hàng sẽ chọn sau khi đăng nhập");
            return;
        }

        String diaChiDaLuu = sessionManager.getDiaChiCheckout();
        if (!TextUtils.isEmpty(diaChiDaLuu)) {
            diaChiRepository.layDiaChiTheoId(diaChiDaLuu, new DiaChiRepository.DiaChiItemCallback() {
                @Override
                public void onSuccess(DiaChi diaChi) {
                    if (diaChi != null) {
                        diaChiDangChon = diaChi;
                        hienThiDiaChi(diaChiDangChon);
                    } else {
                        taiDiaChiMacDinh();
                    }
                }

                @Override
                public void onError(Exception e) {
                    taiDiaChiMacDinh();
                }
            });
        } else {
            taiDiaChiMacDinh();
        }
    }

    private void taiDiaChiMacDinh() {
        diaChiRepository.layDiaChiMacDinh(nguoiDungRepository.getCurrentUser().getUid(), new DiaChiRepository.DiaChiItemCallback() {
            @Override
            public void onSuccess(DiaChi diaChi) {
                diaChiDangChon = diaChi;
                if (diaChiDangChon != null) {
                    sessionManager.setDiaChiCheckout(diaChiDangChon.getDiaChiId());
                    hienThiDiaChi(diaChiDangChon);
                } else {
                    tvAddressType.setText("Chưa có địa chỉ");
                    tvAddressValue.setText("Nhấn biểu tượng bút để chọn địa chỉ giao hàng");
                }
            }

            @Override
            public void onError(Exception e) {
                tvAddressType.setText("Không tải được địa chỉ");
                tvAddressValue.setText("Vui lòng thử lại");
            }
        });
    }

    private void hienThiDiaChi(DiaChi diaChi) {
        tvAddressType.setText(diaChi.getTenNguoiNhan());
        tvAddressValue.setText(diaChi.getDiaChi() + " | " + diaChi.getSoDienThoai());
    }

    private void capNhatThongTinTien() {
        int tongThanhToan = tongTienHang + phiShip;

        tvAmountValue.setText(FormatUtils.formatCurrency(tongTienHang));
        tvShippingValue.setText(FormatUtils.formatCurrency(phiShip));
        tvTotalValue.setText(FormatUtils.formatCurrency(tongThanhToan));
    }

    private void capNhatHienThiShip() {
        tvShippingChoice.setText(tenShip + " - " + FormatUtils.formatCurrency(phiShip));
    }

    private void applyInsets() {
        View root = findViewById(R.id.checkoutRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
