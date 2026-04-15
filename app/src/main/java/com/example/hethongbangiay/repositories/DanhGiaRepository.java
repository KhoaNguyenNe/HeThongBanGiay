package com.example.hethongbangiay.repositories;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.models.DanhGia;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DanhGiaRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void layDanhGiaTheoSanPhamId(String sanPhamId, @NonNull OnFirestoreResult<List<DanhGia>> listener) {
        db.collection("SanPham")
                .document(sanPhamId)
                .collection("DanhGia")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DanhGia> list = new ArrayList<>();
                    for (var doc : snapshot.getDocuments()) {
                        list.add(FirestoreMapper.toDanhGia(doc, sanPhamId));
                    }
                    listener.onSuccess(list);
                })
                .addOnFailureListener(listener::onError);
    }

    public void layDiemTrungBinh(String sanPhamId, @NonNull OnFirestoreResult<Float> listener) {
        layDanhGiaTheoSanPhamId(sanPhamId, new OnFirestoreResult<List<DanhGia>>() {
            @Override
            public void onSuccess(List<DanhGia> data) {
                if (data == null || data.isEmpty()) {
                    listener.onSuccess(0f);
                    return;
                }

                float tong = 0f;
                for (DanhGia dg : data) {
                    tong += dg.getRating();
                }
                listener.onSuccess(tong / data.size());
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    public void demSoDanhGia(String sanPhamId, @NonNull OnFirestoreResult<Integer> listener) {
        layDanhGiaTheoSanPhamId(sanPhamId, new OnFirestoreResult<List<DanhGia>>() {
            @Override
            public void onSuccess(List<DanhGia> data) {
                listener.onSuccess(data == null ? 0 : data.size());
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }
}