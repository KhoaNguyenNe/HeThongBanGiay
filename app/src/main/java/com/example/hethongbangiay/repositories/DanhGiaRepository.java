package com.example.hethongbangiay.repositories;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.models.DanhGia;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DanhGiaRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // THÊM ĐÁNH GIÁ
    public void themDanhGia(DanhGia danhGia,
                            @NonNull OnFirestoreResult<Void> listener) {

        if (danhGia == null) {
            listener.onError(new Exception("Dữ liệu rỗng"));
            return;
        }

        if (danhGia.getSanPhamId() == null ||
                danhGia.getSanPhamId().trim().isEmpty()) {
            listener.onError(new Exception("Thiếu sản phẩm"));
            return;
        }

        if (danhGia.getNguoiDungId() == null ||
                danhGia.getNguoiDungId().trim().isEmpty()) {
            listener.onError(new Exception("Thiếu người dùng"));
            return;
        }

        if (danhGia.getRating() < 1 || danhGia.getRating() > 5) {
            listener.onError(new Exception("Rating từ 1 đến 5"));
            return;
        }

        db.collection("DanhGia")
                .add(danhGia)
                .addOnSuccessListener(doc -> {

                    doc.update("danhGiaId", doc.getId())
                            .addOnSuccessListener(unused ->
                                    listener.onSuccess(null))
                            .addOnFailureListener(listener::onError);

                })
                .addOnFailureListener(listener::onError);
    }


    // LẤY THEO SẢN PHẨM
    public void layDanhGiaTheoSanPhamId(String sanPhamId,
                                        @NonNull OnFirestoreResult<List<DanhGia>> listener) {

        db.collection("DanhGia")
                .whereEqualTo("sanPhamId", sanPhamId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<DanhGia> list = new ArrayList<>();

                    for (var doc : snapshot.getDocuments()) {
                        DanhGia dg = doc.toObject(DanhGia.class);

                        if (dg != null) {
                            dg.setDanhGiaId(doc.getId());
                            list.add(dg);
                        }
                    }

                    listener.onSuccess(list);

                })
                .addOnFailureListener(listener::onError);
    }


    // ĐIỂM TB
    public void layDiemTrungBinh(String sanPhamId,
                                 @NonNull OnFirestoreResult<Float> listener) {

        layDanhGiaTheoSanPhamId(sanPhamId, new OnFirestoreResult<List<DanhGia>>() {
            @Override
            public void onSuccess(List<DanhGia> data) {

                if (data == null || data.isEmpty()) {
                    listener.onSuccess(0f);
                    return;
                }

                float tong = 0;

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


    // ĐẾM SỐ REVIEW
    public void demSoDanhGia(String sanPhamId,
                             @NonNull OnFirestoreResult<Integer> listener) {

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