package com.example.hethongbangiay.repositories;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.models.SanPham;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SanPhamRepository {

    public static final String SORT_SP_THEM_VAO_MOI_NHAT = "new_recent";
    public static final String SORT_SP_BAN_CHAY = "popular";
    public static final String SORT_GIA_CAO_NHAT = "price_high";
    public static final String SORT_GIA_THAP_NHAT = "price_low";
    public static final String SORT_XEP_HANG = "rating";

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void layGiaMax(@NonNull OnFirestoreResult<Double> listener) {
        db.collection("SanPham")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    double max = 0;
                    for (var doc : snapshot.getDocuments()) {
                        SanPham sp = FirestoreMapper.toSanPham(doc);
                        if (sp.getDonGia() > max) {
                            max = sp.getDonGia();
                        }
                    }
                    listener.onSuccess(max);
                })
                .addOnFailureListener(listener::onError);
    }

    public void layTatCaSpDangActive(@NonNull OnFirestoreResult<List<SanPham>> listener) {
        timKiemSanPham("", null, 0, 0, 0, SORT_SP_THEM_VAO_MOI_NHAT, listener);
    }

    public void timKiemSpTheoId(String idSp, @NonNull OnFirestoreResult<SanPham> listener) {
        db.collection("SanPham")
                .document(idSp)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        listener.onSuccess(null);
                        return;
                    }
                    listener.onSuccess(FirestoreMapper.toSanPham(doc));
                })
                .addOnFailureListener(listener::onError);
    }

    public void timKiemSanPham(String tuKhoa,
                               String danhMucId,
                               double giaMin,
                               double giaMax,
                               double diemDanhGiaMin,
                               String sortBy,
                               @NonNull OnFirestoreResult<List<SanPham>> listener) {

        db.collection("SanPham")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<SanPham> data = new ArrayList<>();
                    String keyword = tuKhoa == null ? "" : tuKhoa.trim().toLowerCase(Locale.ROOT);

                    for (var doc : snapshot.getDocuments()) {
                        SanPham sp = FirestoreMapper.toSanPham(doc);

                        if (danhMucId != null && !danhMucId.trim().isEmpty()
                                && !danhMucId.equals(sp.getDanhMucId())) {
                            continue;
                        }

                        if (!keyword.isEmpty()) {
                            String ten = sp.getTenSanPham() == null ? "" : sp.getTenSanPham().toLowerCase(Locale.ROOT);
                            String moTa = sp.getMoTaSanPham() == null ? "" : sp.getMoTaSanPham().toLowerCase(Locale.ROOT);
                            if (!ten.contains(keyword) && !moTa.contains(keyword)) {
                                continue;
                            }
                        }

                        if (giaMin > 0 && sp.getDonGia() < giaMin) {
                            continue;
                        }

                        if (giaMax > 0 && sp.getDonGia() > giaMax) {
                            continue;
                        }

                        if (diemDanhGiaMin > 0 && sp.getDiemDanhGia() < diemDanhGiaMin) {
                            continue;
                        }

                        data.add(sp);
                    }

                    sapXep(data, sortBy);
                    listener.onSuccess(data);
                })
                .addOnFailureListener(listener::onError);
    }

    private void sapXep(List<SanPham> data, String sortBy) {
        if (SORT_SP_BAN_CHAY.equals(sortBy)) {
            data.sort((a, b) -> Integer.compare(b.getLuotBan(), a.getLuotBan()));
            return;
        }

        if (SORT_GIA_CAO_NHAT.equals(sortBy)) {
            data.sort((a, b) -> Double.compare(b.getDonGia(), a.getDonGia()));
            return;
        }

        if (SORT_GIA_THAP_NHAT.equals(sortBy)) {
            data.sort(Comparator.comparingDouble(SanPham::getDonGia));
            return;
        }

        if (SORT_XEP_HANG.equals(sortBy)) {
            data.sort((a, b) -> Double.compare(b.getDiemDanhGia(), a.getDiemDanhGia()));
            return;
        }

        // new_recent: tạm ưu tiên id giảm dần nếu chưa dùng timestamp thật
        data.sort((a, b) -> {
            String idA = a.getSanPhamId() == null ? "" : a.getSanPhamId();
            String idB = b.getSanPhamId() == null ? "" : b.getSanPhamId();
            return idB.compareToIgnoreCase(idA);
        });
    }
}