package com.example.hethongbangiay.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.hethongbangiay.activities.auth.LoginActivity;
import com.example.hethongbangiay.activities.auth.RegisterActivity;
import com.example.hethongbangiay.adapters.PaymentOptionAdapter;
import com.example.hethongbangiay.database.GioHangDB;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.models.DiaChi;
import com.example.hethongbangiay.repositories.DiaChiRepository;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.example.hethongbangiay.session.SessionManager;
import com.example.hethongbangiay.utils.Constants;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class PaymentMethodActivity extends AppCompatActivity {

    public static final String PHUONG_THUC_COD = "COD";
    public static final String PHUONG_THUC_MOMO = "MOMO";
    public static final String PHUONG_THUC_VNPAY = "VNPAY";

    private ImageView btnBackPayment;
    private RecyclerView rvPaymentOptions;
    private AppCompatButton btnConfirmPayment;
    private PaymentOptionAdapter paymentOptionAdapter;
    private final List<PaymentOptionAdapter.PaymentOption> dsPhuongThuc = new ArrayList<>();

    private SessionManager sessionManager;
    private GioHangDB gioHangDB;
    private NguoiDungRepository nguoiDungRepository;
    private DiaChiRepository diaChiRepository;
    private FirebaseFirestore db;

    private String phuongThucDangChon = PHUONG_THUC_COD;
    private boolean dangTaoDonHang = false;
    private final ActivityResultLauncher<Intent> chonDiaChiLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    DiaChi diaChi = (DiaChi) result.getData().getSerializableExtra(ShippingAddressActivity.EXTRA_DIA_CHI_DA_CHON);
                    if (diaChi != null) {
                        sessionManager.setDiaChiCheckout(diaChi.getDiaChiId());
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_payment_method);

        initViews();
        initObjects();
        applyInsets();
        capNhatLuaChon(phuongThucDangChon);
        initEvents();
    }

    private void initViews() {
        btnBackPayment = findViewById(R.id.btnBackPayment);
        rvPaymentOptions = findViewById(R.id.rvPaymentOptions);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);

        paymentOptionAdapter = new PaymentOptionAdapter(option -> capNhatLuaChon(option.getMa()));
        rvPaymentOptions.setLayoutManager(new LinearLayoutManager(this));
        rvPaymentOptions.setAdapter(paymentOptionAdapter);

        dsPhuongThuc.clear();
        dsPhuongThuc.add(new PaymentOptionAdapter.PaymentOption(PHUONG_THUC_COD, "Thanh toán khi nhận hàng", "", R.drawable.ic_wallet));
        dsPhuongThuc.add(new PaymentOptionAdapter.PaymentOption(PHUONG_THUC_MOMO, "MoMo", "", R.drawable.ic_google));
        dsPhuongThuc.add(new PaymentOptionAdapter.PaymentOption(PHUONG_THUC_VNPAY, "VNPAY", "", R.drawable.ic_mastercard));
    }

    private void initObjects() {
        sessionManager = new SessionManager(this);
        gioHangDB = new GioHangDB(this);
        nguoiDungRepository = new NguoiDungRepository();
        diaChiRepository = new DiaChiRepository();
        db = FirebaseFirestore.getInstance();
        phuongThucDangChon = sessionManager.getPhuongThucThanhToan();
    }

    private void initEvents() {
        btnBackPayment.setOnClickListener(v -> finish());

        btnConfirmPayment.setOnClickListener(v -> {
            if (gioHangDB.gioHangTrong()) {
                Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
                return;
            }

            sessionManager.setPhuongThucThanhToan(phuongThucDangChon);

            if (!nguoiDungRepository.isUserLoggedIn()) {
                sessionManager.batDauChoXuLyThanhToan();
                hoiTaiKhoanDeDangNhap();
                return;
            }

            xuLyThanhToan();
        });
    }

    private void capNhatLuaChon(String phuongThuc) {
        phuongThucDangChon = phuongThuc;
        sessionManager.setPhuongThucThanhToan(phuongThuc);
        paymentOptionAdapter.capNhatDuLieu(dsPhuongThuc, phuongThucDangChon);
    }

    private void hoiTaiKhoanDeDangNhap() {
        new AlertDialog.Builder(this)
                .setTitle("Bạn đã có tài khoản chưa?")
                .setMessage("Nếu đã có tài khoản, hệ thống sẽ chuyển sang đăng nhập. Nếu chưa có, hệ thống sẽ chuyển sang đăng ký.")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                })
                .setNegativeButton("Đăng ký", (dialog, which) -> {
                    startActivity(new Intent(this, RegisterActivity.class));
                    finish();
                })
                .setCancelable(true)
                .show();
    }

    private void xuLyThanhToan() {
        if (dangTaoDonHang) {
            return;
        }

        if (!nguoiDungRepository.isUserLoggedIn()) {
            sessionManager.batDauChoXuLyThanhToan();
            hoiTaiKhoanDeDangNhap();
            return;
        }

        String diaChiId = sessionManager.getDiaChiCheckout();
        if (!TextUtils.isEmpty(diaChiId)) {
            taoDonHangVaHoaDon(diaChiId);
            return;
        }

        diaChiRepository.layDiaChiMacDinh(nguoiDungRepository.getCurrentUser().getUid(), new DiaChiRepository.DiaChiItemCallback() {
            @Override
            public void onSuccess(DiaChi diaChi) {
                if (diaChi == null) {
                    Toast.makeText(PaymentMethodActivity.this, "Bạn chưa có địa chỉ. Hãy chọn địa chỉ trước khi thanh toán", Toast.LENGTH_SHORT).show();
                    chonDiaChiLauncher.launch(new Intent(PaymentMethodActivity.this, ShippingAddressActivity.class));
                    return;
                }

                sessionManager.setDiaChiCheckout(diaChi.getDiaChiId());
                taoDonHangVaHoaDon(diaChi.getDiaChiId());
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(PaymentMethodActivity.this, "Không tải được địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void taoDonHangVaHoaDon(String diaChiId) {
        List<ChiTietDonHang> dsSanPhamDaChon = sessionManager.getGioHangDangChon();
        boolean hasSelected = dsSanPhamDaChon != null && !dsSanPhamDaChon.isEmpty();

        List<ChiTietDonHang> dsSanPham = hasSelected
                ? dsSanPhamDaChon
                : gioHangDB.layTatCaSanPhamTrongGio();
        if (dsSanPham.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        dangTaoDonHang = true;
        btnConfirmPayment.setEnabled(false);

        int tongTienHang;
        if (hasSelected) {
            tongTienHang = 0;
            for (ChiTietDonHang item : dsSanPham) {
                if (item != null) tongTienHang += (int) item.getGiaTien();
            }
        } else {
            tongTienHang = gioHangDB.tongTienGioHang();
        }
        int phiShip = sessionManager.getPhiShip();
        int tongThanhToan = tongTienHang + phiShip;

        String nguoiDungId = nguoiDungRepository.getCurrentUser().getUid();
        String donHangId = db.collection("DonHang").document().getId();
        String hoaDonId = db.collection("HoaDon").document().getId();

        DonHang donHang = new DonHang();
        donHang.setDonHangId(donHangId);
        donHang.setNguoiDungId(nguoiDungId);
        donHang.setNgayDatHang(Timestamp.now());
        donHang.setTinhTrangDonHang(Constants.CHO_XAC_NHAN);
        donHang.setNgayGiaoHang(null);
        donHang.setNgayHuy(null);
        donHang.setChiTietSanPham(dsSanPham);

        WriteBatch batch = db.batch();
        DocumentReference donHangRef = db.collection("DonHang").document(donHangId);
        batch.set(donHangRef, donHang);

        for (ChiTietDonHang item : dsSanPham) {
            DocumentReference chiTietRef = db.collection("ChiTietDonHang").document();
            Map<String, Object> chiTietMap = new HashMap<>();
            chiTietMap.put("chiTietDonHangId", chiTietRef.getId());
            chiTietMap.put("donHangId", donHangId);
            chiTietMap.put("tenSanPham", item.getTenSanPham());
            chiTietMap.put("giaTien", item.getGiaTien());
            chiTietMap.put("sizeGiay", item.getSizeGiay());
            chiTietMap.put("mauSac", item.getMauSac());
            chiTietMap.put("soLuong", item.getSoLuong());
            chiTietMap.put("anhSanPham", item.getAnhSanPham());
            batch.set(chiTietRef, chiTietMap);
        }

        Map<String, Object> hoaDonMap = new HashMap<>();
        hoaDonMap.put("hoaDonId", hoaDonId);
        hoaDonMap.put("diaChiId", diaChiId);
        hoaDonMap.put("donHangId", donHangId);
        hoaDonMap.put("tongTienThanhToan", tongThanhToan);
        hoaDonMap.put("ngayLapHoaDon", Timestamp.now());
        hoaDonMap.put("tienShip", phiShip);
        hoaDonMap.put("phuongThucThanhToan", phuongThucDangChon);

        if (PHUONG_THUC_COD.equals(phuongThucDangChon)) {
            hoaDonMap.put("trangThaiThanhToan", Constants.CHUA_THANH_TOAN);
            hoaDonMap.put("maGiaoDich", "");
        } else {
            // MoMo/VNPAY thật sẽ cần SDK hoặc API ngoài phần lý thuyết hiện tại.
            // Ở đây mình lưu phương thức đã chọn và mô phỏng mã giao dịch thành công.
            hoaDonMap.put("trangThaiThanhToan", Constants.DA_THANH_TOAN);
            hoaDonMap.put("maGiaoDich", taoMaGiaoDichAo(phuongThucDangChon));
        }

        batch.set(db.collection("HoaDon").document(hoaDonId), hoaDonMap);

        batch.commit()
                .addOnSuccessListener(unused -> {
                    if (hasSelected) {
                        for (ChiTietDonHang item : dsSanPham) {
                            gioHangDB.xoaSanPhamTrongGio(item);
                        }
                    } else {
                        gioHangDB.xoaTatCaGioHang();
                    }
                    sessionManager.xoaThongTinTamCheckout();
                    Toast.makeText(this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, OrderSuccessfulActivity.class);
                    intent.putExtra(OrderSuccessfulActivity.EXTRA_DON_HANG_ID, donHangId);
                    intent.putExtra(OrderSuccessfulActivity.EXTRA_HOA_DON_ID, hoaDonId);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    dangTaoDonHang = false;
                    btnConfirmPayment.setEnabled(true);
                    Toast.makeText(this, "Không tạo được đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String taoMaGiaoDichAo(String phuongThuc) {
        return phuongThuc + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private void applyInsets() {
        View root = findViewById(R.id.paymentRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
