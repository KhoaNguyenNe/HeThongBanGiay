package com.example.hethongbangiay.repositories;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.models.DanhGia;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

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

        if (danhGia.getDonHangId() == null ||
                danhGia.getDonHangId().trim().isEmpty()) {
            listener.onError(new Exception("Thiếu đơn hàng"));
            return;
        }

        if (danhGia.getRating() < 1 || danhGia.getRating() > 5) {
            listener.onError(new Exception("Rating từ 1 đến 5"));
            return;
        }

        String docId = buildDanhGiaId(danhGia.getDonHangId(), danhGia.getSanPhamId(), danhGia.getNguoiDungId());
        db.collection("DanhGia")
                .document(docId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot != null && snapshot.exists()) {
                        listener.onError(new Exception("Sản phẩm trong đơn hàng này đã được đánh giá"));
                        return;
                    }

                    danhGia.setDanhGiaId(docId);
                    db.collection("DanhGia")
                            .document(docId)
                            .set(danhGia, SetOptions.merge())
                            .addOnSuccessListener(unused -> {
                                capNhatDiemDanhGiaSanPham(danhGia.getSanPhamId(), new OnFirestoreResult<Void>() {
                                    @Override
                                    public void onSuccess(Void data) {
                                        listener.onSuccess(null);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        // Nếu cập nhật điểm trung bình thất bại, vẫn báo lỗi để UI không đóng sớm
                                        listener.onError(e);
                                    }
                                });
                            })
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    private void capNhatDiemDanhGiaSanPham(String sanPhamId,
                                           @NonNull OnFirestoreResult<Void> listener) {
        if (sanPhamId == null || sanPhamId.trim().isEmpty()) {
            listener.onError(new Exception("Thiếu sản phẩm"));
            return;
        }

        layDiemTrungBinh(sanPhamId, new OnFirestoreResult<Float>() {
            @Override
            public void onSuccess(Float data) {
                float diemTB = data == null ? 0f : data;
                // Ưu tiên update theo docId = sanPhamId.
                // Nếu docId không trùng (một số dữ liệu seed dùng docId khác), fallback query theo field sanPhamId.
                db.collection("SanPham")
                        .document(sanPhamId)
                        .update("diemDanhGia", (double) diemTB)
                        .addOnSuccessListener(unused -> listener.onSuccess(null))
                        .addOnFailureListener(primaryErr -> db.collection("SanPham")
                                .whereEqualTo("sanPhamId", sanPhamId)
                                .limit(1)
                                .get()
                                .addOnSuccessListener(snapshot -> {
                                    if (snapshot == null || snapshot.isEmpty()) {
                                        listener.onError(primaryErr);
                                        return;
                                    }
                                    snapshot.getDocuments()
                                            .get(0)
                                            .getReference()
                                            .update("diemDanhGia", (double) diemTB)
                                            .addOnSuccessListener(unused -> listener.onSuccess(null))
                                            .addOnFailureListener(listener::onError);
                                })
                                .addOnFailureListener(listener::onError));
            }

            @Override
            public void onError(Exception e) {
                listener.onError(e);
            }
        });
    }

    public void daDanhGiaTrongDonHang(String donHangId,
                                     String sanPhamId,
                                     String nguoiDungId,
                                     @NonNull OnFirestoreResult<Boolean> listener) {
        if (donHangId == null || donHangId.trim().isEmpty()
                || sanPhamId == null || sanPhamId.trim().isEmpty()
                || nguoiDungId == null || nguoiDungId.trim().isEmpty()) {
            listener.onSuccess(false);
            return;
        }

        String docId = buildDanhGiaId(donHangId, sanPhamId, nguoiDungId);
        db.collection("DanhGia")
                .document(docId)
                .get()
                .addOnSuccessListener(snapshot -> listener.onSuccess(snapshot != null && snapshot.exists()))
                .addOnFailureListener(listener::onError);
    }

    public static String buildDanhGiaId(String donHangId, String sanPhamId, String nguoiDungId) {
        return (donHangId == null ? "" : donHangId.trim())
                + "_"
                + (sanPhamId == null ? "" : sanPhamId.trim())
                + "_"
                + (nguoiDungId == null ? "" : nguoiDungId.trim());
    }


    // LẤY THEO SẢN PHẨM
    public void layDanhGiaTheoSanPhamId(String sanPhamId,
                                        @NonNull OnFirestoreResult<List<DanhGia>> listener) {

        db.collection("DanhGia")
                .whereEqualTo("sanPhamId", sanPhamId)
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<DanhGia> list = new ArrayList<>();

                    for (com.google.firebase.firestore.DocumentSnapshot doc : snapshot.getDocuments()) {
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