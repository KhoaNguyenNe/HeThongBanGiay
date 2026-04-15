package com.example.hethongbangiay.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.DiaChi;
import com.example.hethongbangiay.repositories.DiaChiRepository;
import com.example.hethongbangiay.repositories.NguoiDungRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

public class AddressFormActivity extends AppCompatActivity {

    public static final String EXTRA_DIA_CHI = "extra_dia_chi";

    private ImageView btnBackAddressForm;
    private TextView tvAddressFormTitle;
    private TextInputEditText edtTenNguoiNhan;
    private TextInputEditText edtSoDienThoaiDiaChi;
    private TextInputEditText edtDiaChiDayDu;
    private MaterialCheckBox cbMacDinh;
    private MaterialButton btnSaveAddress;

    private DiaChiRepository diaChiRepository;
    private NguoiDungRepository nguoiDungRepository;
    private DiaChi diaChiDangSua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_address_form);

        initViews();
        initObjects();
        initEvents();
        applyInsets();
        bindDuLieuNeuCo();
    }

    private void initViews() {
        btnBackAddressForm = findViewById(R.id.btnBackAddressForm);
        tvAddressFormTitle = findViewById(R.id.tvAddressFormTitle);
        edtTenNguoiNhan = findViewById(R.id.edtTenNguoiNhan);
        edtSoDienThoaiDiaChi = findViewById(R.id.edtSoDienThoaiDiaChi);
        edtDiaChiDayDu = findViewById(R.id.edtDiaChiDayDu);
        cbMacDinh = findViewById(R.id.cbMacDinh);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);
    }

    private void initObjects() {
        diaChiRepository = new DiaChiRepository();
        nguoiDungRepository = new NguoiDungRepository();
        diaChiDangSua = (DiaChi) getIntent().getSerializableExtra(EXTRA_DIA_CHI);
    }

    private void initEvents() {
        btnBackAddressForm.setOnClickListener(v -> finish());
        btnSaveAddress.setOnClickListener(v -> luuDiaChi());
    }

    private void bindDuLieuNeuCo() {
        if (diaChiDangSua == null) {
            tvAddressFormTitle.setText("Thêm địa chỉ");
            btnSaveAddress.setText("Lưu địa chỉ");
            return;
        }

        tvAddressFormTitle.setText("Sửa địa chỉ");
        btnSaveAddress.setText("Cập nhật địa chỉ");
        edtTenNguoiNhan.setText(diaChiDangSua.getTenNguoiNhan());
        edtSoDienThoaiDiaChi.setText(diaChiDangSua.getSoDienThoai());
        edtDiaChiDayDu.setText(diaChiDangSua.getDiaChi());
        cbMacDinh.setChecked(diaChiDangSua.isMacDinh());
    }

    private void luuDiaChi() {
        if (!nguoiDungRepository.isUserLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu địa chỉ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String tenNguoiNhan = layText(edtTenNguoiNhan);
        String soDienThoai = layText(edtSoDienThoaiDiaChi);
        String diaChiDayDu = layText(edtDiaChiDayDu);

        if (tenNguoiNhan.isEmpty()) {
            edtTenNguoiNhan.setError("Nhập tên người nhận");
            edtTenNguoiNhan.requestFocus();
            return;
        }

        if (soDienThoai.isEmpty() || soDienThoai.length() < 9) {
            edtSoDienThoaiDiaChi.setError("Số điện thoại chưa hợp lệ");
            edtSoDienThoaiDiaChi.requestFocus();
            return;
        }

        if (diaChiDayDu.isEmpty()) {
            edtDiaChiDayDu.setError("Nhập địa chỉ giao hàng");
            edtDiaChiDayDu.requestFocus();
            return;
        }

        btnSaveAddress.setEnabled(false);

        DiaChi diaChi = diaChiDangSua == null ? new DiaChi() : diaChiDangSua;
        diaChi.setNguoiDungId(nguoiDungRepository.getCurrentUser().getUid());
        diaChi.setTenNguoiNhan(tenNguoiNhan);
        diaChi.setSoDienThoai(soDienThoai);
        diaChi.setDiaChi(diaChiDayDu);
        diaChi.setMacDinh(cbMacDinh.isChecked());

        DiaChiRepository.ActionCallback callback = new DiaChiRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                setResult(RESULT_OK);
                Toast.makeText(AddressFormActivity.this, diaChiDangSua == null ? "Đã thêm địa chỉ" : "Đã cập nhật địa chỉ", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                btnSaveAddress.setEnabled(true);
                Toast.makeText(AddressFormActivity.this, "Không lưu được địa chỉ", Toast.LENGTH_SHORT).show();
            }
        };

        if (diaChiDangSua == null) {
            diaChiRepository.themDiaChi(diaChi, callback);
        } else {
            diaChiRepository.capNhatDiaChi(diaChi, callback);
        }
    }

    private String layText(TextInputEditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private void applyInsets() {
        View root = findViewById(R.id.addressFormRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
