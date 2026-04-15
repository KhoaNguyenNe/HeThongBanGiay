package com.example.hethongbangiay.repositories;

import com.example.hethongbangiay.models.DiaChi;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class DiaChiRepository {

    public interface DiaChiListCallback {
        void onSuccess(List<DiaChi> dsDiaChi);

        void onError(Exception e);
    }

    public interface DiaChiItemCallback {
        void onSuccess(DiaChi diaChi);

        void onError(Exception e);
    }

    public interface ActionCallback {
        void onSuccess();

        void onError(Exception e);
    }

    private final FirebaseFirestore db;

    public DiaChiRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void layDiaChiTheoNguoiDung(String nguoiDungId, DiaChiListCallback callback) {
        db.collection("DiaChi")
                .whereEqualTo("nguoiDungId", nguoiDungId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DiaChi> data = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        DiaChi diaChi = snapshot.toObject(DiaChi.class);
                        if (diaChi != null) {
                            diaChi.setDiaChiId(snapshot.getId());
                            data.add(diaChi);
                        }
                    }
                    callback.onSuccess(data);
                })
                .addOnFailureListener(callback::onError);
    }

    public void layDiaChiMacDinh(String nguoiDungId, DiaChiItemCallback callback) {
        layDiaChiTheoNguoiDung(nguoiDungId, new DiaChiListCallback() {
            @Override
            public void onSuccess(List<DiaChi> dsDiaChi) {
                DiaChi diaChiMacDinh = null;
                for (DiaChi item : dsDiaChi) {
                    if (item.isMacDinh()) {
                        diaChiMacDinh = item;
                        break;
                    }
                }

                if (diaChiMacDinh == null && !dsDiaChi.isEmpty()) {
                    diaChiMacDinh = dsDiaChi.get(0);
                }

                callback.onSuccess(diaChiMacDinh);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void layDiaChiTheoId(String diaChiId, DiaChiItemCallback callback) {
        db.collection("DiaChi")
                .document(diaChiId)
                .get()
                .addOnSuccessListener(snapshot -> {
                    DiaChi diaChi = snapshot.toObject(DiaChi.class);
                    if (diaChi != null) {
                        diaChi.setDiaChiId(snapshot.getId());
                    }
                    callback.onSuccess(diaChi);
                })
                .addOnFailureListener(callback::onError);
    }

    public void themDiaChi(DiaChi diaChi, ActionCallback callback) {
        String documentId = db.collection("DiaChi").document().getId();
        diaChi.setDiaChiId(documentId);
        layDiaChiTheoNguoiDung(diaChi.getNguoiDungId(), new DiaChiListCallback() {
            @Override
            public void onSuccess(List<DiaChi> dsDiaChi) {
                if (dsDiaChi.isEmpty()) {
                    diaChi.setMacDinh(true);
                }

                if (diaChi.isMacDinh()) {
                    datMacDinhVaLuu(diaChi, callback);
                    return;
                }

                db.collection("DiaChi")
                        .document(documentId)
                        .set(diaChi)
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void capNhatDiaChi(DiaChi diaChi, ActionCallback callback) {
        if (diaChi == null || diaChi.getDiaChiId() == null || diaChi.getDiaChiId().trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("Địa chỉ không hợp lệ"));
            return;
        }

        if (diaChi.isMacDinh()) {
            datMacDinhVaLuu(diaChi, callback);
            return;
        }

        layDiaChiTheoNguoiDung(diaChi.getNguoiDungId(), new DiaChiListCallback() {
            @Override
            public void onSuccess(List<DiaChi> dsDiaChi) {
                boolean coMacDinhKhac = false;
                for (DiaChi item : dsDiaChi) {
                    if (!item.getDiaChiId().equals(diaChi.getDiaChiId()) && item.isMacDinh()) {
                        coMacDinhKhac = true;
                        break;
                    }
                }

                if (!coMacDinhKhac) {
                    diaChi.setMacDinh(true);
                    datMacDinhVaLuu(diaChi, callback);
                    return;
                }

                db.collection("DiaChi")
                        .document(diaChi.getDiaChiId())
                        .set(diaChi)
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public void xoaDiaChi(String nguoiDungId, String diaChiId, ActionCallback callback) {
        db.collection("DiaChi")
                .document(diaChiId)
                .delete()
                .addOnSuccessListener(unused -> layDiaChiTheoNguoiDung(nguoiDungId, new DiaChiListCallback() {
                    @Override
                    public void onSuccess(List<DiaChi> dsDiaChi) {
                        if (dsDiaChi.isEmpty()) {
                            callback.onSuccess();
                            return;
                        }

                        boolean coMacDinh = false;
                        for (DiaChi item : dsDiaChi) {
                            if (item.isMacDinh()) {
                                coMacDinh = true;
                                break;
                            }
                        }

                        if (coMacDinh) {
                            callback.onSuccess();
                        } else {
                            DiaChi diaChiMacDinh = dsDiaChi.get(0);
                            diaChiMacDinh.setMacDinh(true);
                            datMacDinhVaLuu(diaChiMacDinh, callback);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        callback.onError(e);
                    }
                }))
                .addOnFailureListener(callback::onError);
    }

    private void datMacDinhVaLuu(DiaChi diaChi, ActionCallback callback) {
        layDiaChiTheoNguoiDung(diaChi.getNguoiDungId(), new DiaChiListCallback() {
            @Override
            public void onSuccess(List<DiaChi> dsDiaChi) {
                WriteBatch batch = db.batch();

                for (DiaChi item : dsDiaChi) {
                    batch.update(
                            db.collection("DiaChi").document(item.getDiaChiId()),
                            "macDinh",
                            false
                    );
                }

                batch.set(db.collection("DiaChi").document(diaChi.getDiaChiId()), diaChi);
                batch.commit()
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(callback::onError);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }
}
