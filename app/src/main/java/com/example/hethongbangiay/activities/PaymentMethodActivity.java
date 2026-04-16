package com.example.hethongbangiay.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
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

import com.example.hethongbangiay.Api.CreateOrder;
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
import com.example.hethongbangiay.viewmodels.OrderViewModel;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.widget.AppCompatButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import vn.zalopay.sdk.Environment;
import vn.zalopay.sdk.ZaloPayError;
import vn.zalopay.sdk.ZaloPaySDK;
import vn.zalopay.sdk.listeners.PayOrderListener;

public class PaymentMethodActivity extends AppCompatActivity {

    public static final String PHUONG_THUC_COD = "COD";
    public static final String PHUONG_THUC_ZALOPAY = "ZALOPAY";

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
    private boolean daTuDongXuLy = false;
    double totalAmount;
    String totalString;

    OrderViewModel orderViewModel;


    private final ActivityResultLauncher<Intent> chonDiaChiLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    DiaChi diaChi = (DiaChi) result.getData().getSerializableExtra(ShippingAddressActivity.EXTRA_DIA_CHI_DA_CHON);
                    if (diaChi != null) {
                        sessionManager.setDiaChiCheckout(diaChi.getDiaChiId());
                        if (sessionManager.dangChoXuLyThanhToan()) {
                            xuLyThanhToan();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_payment_method);

        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
        ZaloPaySDK.init(2553, Environment.SANDBOX);
        totalAmount = getIntent().getIntExtra("tongTien", 0);
        totalString = String.format("%.0f", totalAmount);

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
        dsPhuongThuc.add(new PaymentOptionAdapter.PaymentOption(PHUONG_THUC_ZALOPAY, "ZaloPay", "", R.drawable.ic_mastercard));
    }

    private void initObjects() {
        sessionManager = new SessionManager(this);
        gioHangDB = new GioHangDB(this);
        nguoiDungRepository = new NguoiDungRepository();
        diaChiRepository = new DiaChiRepository();
        db = FirebaseFirestore.getInstance();
        phuongThucDangChon = sessionManager.getPhuongThucThanhToan();
        if ("MOMO".equals(phuongThucDangChon)) {
            phuongThucDangChon = PHUONG_THUC_COD;
            sessionManager.setPhuongThucThanhToan(PHUONG_THUC_COD);
        }
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
        List<ChiTietDonHang> dsSanPham = gioHangDB.layTatCaSanPhamTrongGio();

        if (dsSanPham.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
            return;
        }

        dangTaoDonHang = true;
        btnConfirmPayment.setEnabled(false);

        int tongTienHang = gioHangDB.tongTienGioHang();
        int phiShip = sessionManager.getPhiShip();

        int tongThanhToan = tongTienHang + phiShip;
        if (tongThanhToan < 0) tongThanhToan = 0;

        String nguoiDungId = nguoiDungRepository.getCurrentUser().getUid();
        String donHangId = db.collection("DonHang").document().getId();
        String hoaDonId = db.collection("HoaDon").document().getId();

        // ================== TẠO OBJECT ĐƠN HÀNG ==================
        DonHang donHang = new DonHang();
        donHang.setDonHangId(donHangId);
        donHang.setNguoiDungId(nguoiDungId);
        donHang.setNgayDatHang(Timestamp.now());
        donHang.setTinhTrangDonHang(Constants.CHO_XAC_NHAN);
        donHang.setNgayGiaoHang(null);
        donHang.setNgayHuy(null);
//        donHang.setChiTietSanPham(dsSanPham);
        donHang.setPhuongThucThanhToan(phuongThucDangChon);
        donHang.setTongTien((double) tongThanhToan);

        // ================== CHI TIẾT HÓA ĐƠN ==================
        Map<String, Object> hoaDonMap = new HashMap<>();
        hoaDonMap.put("hoaDonId", hoaDonId);
        hoaDonMap.put("donHangId", donHangId);
        hoaDonMap.put("diaChiId", diaChiId);
        hoaDonMap.put("tongTienThanhToan", tongThanhToan);
        hoaDonMap.put("tienShip", phiShip);
        hoaDonMap.put("ngayLapHoaDon", Timestamp.now());
        hoaDonMap.put("phuongThucThanhToan", phuongThucDangChon);

        // ======================================================
        // COD
        // ======================================================
        if (PHUONG_THUC_COD.equals(phuongThucDangChon)) {

            hoaDonMap.put("trangThaiThanhToan", Constants.CHUA_THANH_TOAN);
            hoaDonMap.put("maGiaoDich", "");

            WriteBatch batch = db.batch();

            // DonHang
            batch.set(db.collection("DonHang").document(donHangId), donHang);

            // ChiTietDonHang
            for (ChiTietDonHang item : dsSanPham) {
                DocumentReference ref = db.collection("ChiTietDonHang").document();

                Map<String, Object> map = new HashMap<>();
                map.put("chiTietDonHangId", ref.getId());
                map.put("sanPhamId", item.getSanPhamId());
                map.put("donHangId", donHangId);
                map.put("tenSanPham", item.getTenSanPham());
                map.put("giaTien", item.getGiaTien());
                map.put("sizeGiay", item.getSizeGiay());
                map.put("mauSac", item.getMauSac());
                map.put("soLuong", item.getSoLuong());
                map.put("anhSanPham", item.getAnhSanPham());

                batch.set(ref, map);
            }

            // HoaDon
            batch.set(db.collection("HoaDon").document(hoaDonId), hoaDonMap);

            batch.commit()
                    .addOnSuccessListener(unused -> {
                        capNhatTonKhoVaLuotBanSauThanhToan(dsSanPham, () -> {
                            gioHangDB.xoaTatCaGioHang();
                            sessionManager.xoaThongTinTamCheckout();

                            Intent intent = new Intent(this, OrderSuccessfulActivity.class);
                            intent.putExtra(OrderSuccessfulActivity.EXTRA_DON_HANG_ID, donHangId);
                            startActivity(intent);
                            finish();
                        });
                    })
                    .addOnFailureListener(e -> {
                        dangTaoDonHang = false;
                        btnConfirmPayment.setEnabled(true);
                        Toast.makeText(this, "Tạo đơn hàng thất bại", Toast.LENGTH_SHORT).show();
                    });

            return;
        }

        // ======================================================
        // ZALOPAY
        // ======================================================
        try {
            CreateOrder orderApi = new CreateOrder();
            JSONObject data = orderApi.createOrder(totalString);

            if (data.getString("return_code").equals("1")) {

                String token = data.getString("zp_trans_token");

                ZaloPaySDK.getInstance().payOrder(
                        PaymentMethodActivity.this,
                        token,
                        "demozpdk://app",
                        new PayOrderListener() {

                            @Override
                            public void onPaymentSucceeded(String transId, String zpTransToken, String appTransId) {

                                runOnUiThread(() -> {

                                    hoaDonMap.put("trangThaiThanhToan", Constants.DA_THANH_TOAN);
                                    hoaDonMap.put("maGiaoDich", transId);

                                    WriteBatch batch = db.batch();

                                    // DonHang
                                    batch.set(db.collection("DonHang").document(donHangId), donHang);

                                    // ChiTietDonHang
                                    for (ChiTietDonHang item : dsSanPham) {
                                        DocumentReference ref = db.collection("ChiTietDonHang").document();

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("chiTietDonHangId", ref.getId());
                                        map.put("sanPhamId", item.getSanPhamId());
                                        map.put("donHangId", donHangId);
                                        map.put("tenSanPham", item.getTenSanPham());
                                        map.put("giaTien", item.getGiaTien());
                                        map.put("sizeGiay", item.getSizeGiay());
                                        map.put("mauSac", item.getMauSac());
                                        map.put("soLuong", item.getSoLuong());
                                        map.put("anhSanPham", item.getAnhSanPham());

                                        batch.set(ref, map);
                                    }

                                    // HoaDon
                                    batch.set(db.collection("HoaDon").document(hoaDonId), hoaDonMap);

                                    batch.commit()
                                            .addOnSuccessListener(unused -> {
                                                capNhatTonKhoVaLuotBanSauThanhToan(dsSanPham, () -> {
                                                    gioHangDB.xoaTatCaGioHang();
                                                    sessionManager.xoaThongTinTamCheckout();

                                                    Intent intent = new Intent(
                                                            PaymentMethodActivity.this,
                                                            OrderSuccessfulActivity.class
                                                    );
                                                    intent.putExtra(
                                                            OrderSuccessfulActivity.EXTRA_DON_HANG_ID,
                                                            donHangId
                                                    );

                                                    startActivity(intent);
                                                    finish();
                                                });
                                            })
                                            .addOnFailureListener(e -> {
                                                dangTaoDonHang = false;
                                                btnConfirmPayment.setEnabled(true);
                                                Toast.makeText(
                                                        PaymentMethodActivity.this,
                                                        "Lưu đơn hàng thất bại",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            });
                                });
                            }

                            @Override
                            public void onPaymentCanceled(String s, String s1) {
                                runOnUiThread(() -> {
                                    dangTaoDonHang = false;
                                    btnConfirmPayment.setEnabled(true);

                                    Toast.makeText(
                                            PaymentMethodActivity.this,
                                            "Bạn đã hủy thanh toán",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                            }

                            @Override
                            public void onPaymentError(ZaloPayError error, String s, String s1) {
                                runOnUiThread(() -> {
                                    dangTaoDonHang = false;
                                    btnConfirmPayment.setEnabled(true);

                                    Toast.makeText(
                                            PaymentMethodActivity.this,
                                            "Thanh toán thất bại",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                            }
                        }
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            dangTaoDonHang = false;
            btnConfirmPayment.setEnabled(true);

            Toast.makeText(this, "Không thể thanh toán online", Toast.LENGTH_SHORT).show();
        }
    }

    private void capNhatTonKhoVaLuotBanSauThanhToan(List<ChiTietDonHang> dsSanPham, Runnable onDone) {
        if (dsSanPham == null || dsSanPham.isEmpty()) {
            if (onDone != null) onDone.run();
            return;
        }

        // Gộp số lượng theo (sanPhamId + size) và theo sản phẩm (để tăng lượt bán)
        Map<String, Integer> qtyBySpSize = new LinkedHashMap<>();
        Map<String, Integer> qtyBySp = new LinkedHashMap<>();

        for (ChiTietDonHang item : dsSanPham) {
            if (item == null) continue;
            String spId = item.getSanPhamId();
            if (spId == null || spId.trim().isEmpty()) continue;

            int qty = Math.max(0, item.getSoLuong());
            if (qty <= 0) continue;

            String spKey = spId.trim();
            qtyBySp.put(spKey, qtyBySp.getOrDefault(spKey, 0) + qty);

            String sizeKey = spKey + "_" + item.getSizeGiay();
            qtyBySpSize.put(sizeKey, qtyBySpSize.getOrDefault(sizeKey, 0) + qty);
        }

        List<com.google.android.gms.tasks.Task<?>> tasks = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : qtyBySp.entrySet()) {
            String spId = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) continue;
            tasks.add(
                    db.collection("SanPham")
                            .document(spId)
                            .update("luotBan", FieldValue.increment(qty))
            );
        }

        for (Map.Entry<String, Integer> entry : qtyBySpSize.entrySet()) {
            String key = entry.getKey();
            int qty = entry.getValue();
            if (qty <= 0) continue;

            int underscore = key.lastIndexOf('_');
            if (underscore <= 0) continue;
            String spId = key.substring(0, underscore);
            int size;
            try {
                size = Integer.parseInt(key.substring(underscore + 1));
            } catch (NumberFormatException ignored) {
                continue;
            }

            com.google.android.gms.tasks.Task<QuerySnapshot> getSizeTask = db.collection("SanPham")
                    .document(spId)
                    .collection("Sizes")
                    .whereEqualTo("size", size)
                    .limit(1)
                    .get();

            com.google.android.gms.tasks.Task<?> updateTask = getSizeTask.continueWithTask(task -> {
                if (!task.isSuccessful() || task.getResult() == null || task.getResult().isEmpty()) {
                    return com.google.android.gms.tasks.Tasks.forResult(null);
                }

                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                DocumentReference ref = doc.getReference();

                return db.runTransaction(transaction -> {
                    DocumentSnapshot snap = transaction.get(ref);
                    Long current = snap.getLong("soLuong");
                    long currentQty = current == null ? 0L : current;
                    long newQty = currentQty - qty;
                    if (newQty < 0L) newQty = 0L;
                    transaction.update(ref, "soLuong", newQty);
                    return null;
                });
            });

            tasks.add(updateTask);
        }

        com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                .addOnCompleteListener(unused -> {
                    if (onDone != null) onDone.run();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ZaloPaySDK.getInstance().onResult(intent);
    }
}
