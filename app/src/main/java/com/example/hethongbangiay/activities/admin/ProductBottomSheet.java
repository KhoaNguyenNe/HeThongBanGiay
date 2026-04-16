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
import com.example.hethongbangiay.repositories.SanPhamRepository;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_ID = "sanPhamId";
    private String sanPhamId;
    private boolean isUpdating = false;
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
    SanPhamRepository repo = new SanPhamRepository();
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

        listSize = new ArrayList<>();
        adapter = new SizeAdapter(requireContext(), listSize);
        lvSize.setAdapter(adapter);
        loadSanPhamFirebase();
        loadSizeFirebase();
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


        for (SizeGiay s : listSize) {
            if (s.getSize() == size) {
                s.setSoLuong(s.getSoLuong() + qty);
                adapter.notifyDataSetChanged();
                return;
            }
        }

        SizeGiay s = new SizeGiay();
        s.setSizeGiayId(java.util.UUID.randomUUID().toString());
        s.setSanPhamId(sanPhamId);
        s.setSize(size);
        s.setSoLuong(qty);

        listSize.add(s);
        adapter.notifyDataSetChanged();

        edtSize.setText("");
        edtQty.setText("");
    }
    private void updateSanPham() {

        if (isUpdating) return;
        isUpdating = true;

        String ten = edtTenSP.getText().toString().trim();
        String giaStr = edtGia.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();

        if (ten.isEmpty() || giaStr.isEmpty()) {
            Toast.makeText(getContext(), "Thiếu dữ liệu", Toast.LENGTH_SHORT).show();
            isUpdating = false;
            return;
        }

        double gia;
        try {
            gia = Double.parseDouble(giaStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            isUpdating = false;
            return;
        }

        if (currentSP == null) {
            Toast.makeText(getContext(), "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            isUpdating = false;
            return;
        }

        currentSP.setTenSanPham(ten);
        currentSP.setDonGia(gia);
        currentSP.setMoTaSanPham(moTa);

        repo.updateSanPham(
                currentSP,
                listSize,
                new SanPhamRepository.FirestoreCallback() {

                    @Override
                    public void onSuccess() {

                        isUpdating = false;

                        if (!isAdded() || getActivity() == null) return;

                        Toast.makeText(requireContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        dismissAllowingStateLoss();
                    }

                    @Override
                    public void onError(String error) {

                        isUpdating = false;

                        if (!isAdded() || getActivity() == null) return;

                        Toast.makeText(requireContext(), "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void loadSanPhamFirebase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("SanPham")
                .document(sanPhamId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    currentSP = doc.toObject(SanPham.class);

                    if (currentSP != null) {
                        currentSP.setSanPhamId(doc.getId());

                        edtTenSP.setText(currentSP.getTenSanPham());
                        edtGia.setText(String.valueOf(currentSP.getDonGia()));
                        edtMoTa.setText(currentSP.getMoTaSanPham());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi load SP: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void loadSizeFirebase() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("SanPham")
                .document(sanPhamId)
                .collection("sizes")
                .get()
                .addOnSuccessListener(snapshot -> {

                    listSize.clear();

                    for (DocumentSnapshot doc : snapshot) {

                        SizeGiay s = doc.toObject(SizeGiay.class);
                        if (s != null) {
                            s.setSizeGiayId(doc.getId());
                            listSize.add(s);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi load size", Toast.LENGTH_SHORT).show()
                );
    }

}
