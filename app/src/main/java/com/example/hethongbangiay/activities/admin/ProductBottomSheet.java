package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.SizeAdapter;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.database.SizeGiayDB;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.models.SizeGiay;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ProductBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_ID = "sanPhamId";
    private String sanPhamId;

    public static ProductBottomSheet newInstance(String id) {
        ProductBottomSheet sheet = new ProductBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, id);
        sheet.setArguments(bundle);
        return sheet;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sanPhamId = getArguments().getString(ARG_ID);
        }
    }

    EditText edtSize, edtQty;
    Button btnAddSize;
    ListView lvSize;
    EditText edtTenSP, edtGia, edtMoTa;
    Button btnUpdate;
    SanPham currentSP;
    List<SizeGiay> listSize;
    SizeAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_product, container, false);
        edtTenSP = v.findViewById(R.id.edtTenSP);
        edtGia = v.findViewById(R.id.edtGia);
        edtMoTa = v.findViewById(R.id.edtMoTa);
        btnUpdate = v.findViewById(R.id.btnUpdate);
        edtSize = v.findViewById(R.id.edtSize);
        edtQty = v.findViewById(R.id.edtQty);
        btnAddSize = v.findViewById(R.id.btnAddPic);
        lvSize = v.findViewById(R.id.lvSize);

        SizeGiayDB db = new SizeGiayDB(requireContext());
        SanPhamDB dbsp = new SanPhamDB(requireContext());
        currentSP = dbsp.getSanPhamById(sanPhamId);

        if (currentSP != null) {
            edtTenSP.setText(currentSP.getTenSanPham());
            edtGia.setText(String.valueOf(currentSP.getDonGia()));
            edtMoTa.setText(currentSP.getMoTaSanPham());
        }
        listSize = db.getBySanPhamId(sanPhamId);
        if (listSize == null) listSize = new ArrayList<>();

        adapter = new SizeAdapter(requireContext(), listSize);
        lvSize.setAdapter(adapter);

        btnAddSize.setOnClickListener(v1 -> addSize());
        btnUpdate.setOnClickListener(v1 -> updateSanPham());

        return v;

    }

    private void addSize() {

        String sizeStr = edtSize.getText().toString().trim();
        String qtyStr = edtQty.getText().toString().trim();

        if (sizeStr.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(getContext(), "Nhập size + số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int size, qty;

        try {
            size = Integer.parseInt(sizeStr);
            qty = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Sai định dạng", Toast.LENGTH_SHORT).show();
            return;
        }

        SizeGiayDB db = new SizeGiayDB(requireContext());

        for (SizeGiay s : listSize) {
            if (s.getSize() == size) {
                s.setSoLuong(s.getSoLuong() + qty);
                adapter.notifyDataSetChanged();
                return;
            }
        }

        SizeGiay s = new SizeGiay();
        s.setSizeGiayId(db.generateSizeGiayId());
        s.setSanPhamId(sanPhamId);
        s.setSize(size);
        s.setSoLuong(qty);

        db.insertSizeGiay(s);

        listSize.add(s);
        adapter.notifyDataSetChanged();

        edtSize.setText("");
        edtQty.setText("");
    }
    private void updateSanPham() {

        String ten = edtTenSP.getText().toString().trim();
        String giaStr = edtGia.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();

        if (ten.isEmpty() || giaStr.isEmpty()) {
            Toast.makeText(getContext(), "Thiếu dữ liệu", Toast.LENGTH_SHORT).show();
            return;
        }

        double gia;
        try {
            gia = Double.parseDouble(giaStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        SanPhamDB db = new SanPhamDB(requireContext());

        currentSP.setTenSanPham(ten);
        currentSP.setDonGia(gia);
        currentSP.setMoTaSanPham(moTa);

        db.updateSanPham(currentSP);

        Toast.makeText(getContext(), "Đã cập nhật", Toast.LENGTH_SHORT).show();
        if (listener != null) {
            listener.onUpdated();
        }
        dismiss();
    }
    public interface OnProductUpdated {
        void onUpdated();
    }

    private OnProductUpdated listener;

    public void setOnProductUpdated(OnProductUpdated listener) {
        this.listener = listener;
    }
}
