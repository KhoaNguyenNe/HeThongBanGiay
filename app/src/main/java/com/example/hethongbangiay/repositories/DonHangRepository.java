package com.example.hethongbangiay.repositories;

import android.util.Log;

import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DiaChi;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.utils.PhuongThucThanhToan;
import com.example.hethongbangiay.utils.TrangThaiDonHang;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class DonHangRepository {
    private FirebaseFirestore db;

    public DonHangRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public interface OnDataLoaded {
        void onSuccess(List<DonHang> list);

        void onError(Exception e);
    }

    public void getAllDonHang(OnDataLoaded callback) {
        db.collection("DonHang")
                .get()
                .addOnSuccessListener(query -> {

                    List<DonHang> list = new ArrayList<>();

                    for (DocumentSnapshot doc : query) {
                        Log.d("DonHangRepo", "Raw: " + doc.getData());
                        DonHang dh = doc.toObject(DonHang.class);

                        if (dh != null) {
                            dh.setDonHangId(doc.getId());
                            list.add(dh);
                        }
                    }

                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getDonHangTheoNguoiDung(String nguoiDungId, OnDataLoaded callback) {
        db.collection("DonHang")
                .whereEqualTo("nguoiDungId", nguoiDungId)
                .get()
                .addOnSuccessListener(query -> {
                    List<DonHang> list = new ArrayList<>();

                    for (DocumentSnapshot doc : query) {
                        DonHang dh = doc.toObject(DonHang.class);

                        if (dh != null) {
                            dh.setDonHangId(doc.getId());
                            list.add(dh);
                        }
                    }

                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    public interface OnCreateOrderListener {
        void onSuccess(String orderId);

        void onError(Exception e);
    }
    public void createOrder(List<ChiTietDonHang> cart,
                            OnCreateOrderListener listener) {

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            listener.onError(new Exception("Chưa đăng nhập"));
            return;
        }

        double tongTien = 0;

        for (ChiTietDonHang item : cart) {
            tongTien += item.getGiaTien() * item.getSoLuong();
        }

        DocumentReference orderRef = db.collection("DonHang").document();
        String orderId = orderRef.getId();

        DonHang donHang = new DonHang();
        donHang.setDonHangId(orderId);
        donHang.setNguoiDungId(userId);
        donHang.setTongTien(tongTien);
        donHang.setNgayDatHang(Timestamp.now());
        donHang.setTinhTrangDonHang(TrangThaiDonHang.CHO_XAC_NHAN);
        donHang.setPhuongThucThanhToan(PhuongThucThanhToan.COD);

        WriteBatch batch = db.batch();

        // lưu đơn hàng
        batch.set(orderRef, donHang);

        // lưu chi tiết đơn hàng
        for (ChiTietDonHang item : cart) {

            DocumentReference detailRef =
                    db.collection("ChiTietDonHang").document();

            item.setChiTietDonHangId(detailRef.getId());
            item.setDonHangId(orderId);

            batch.set(detailRef, item);
        }

        batch.commit()
                .addOnSuccessListener(unused -> listener.onSuccess(orderId))
                .addOnFailureListener(listener::onError);
    }

    public interface OnDataLoadedSingle {
        void onSuccess(DonHang donHang);
        void onError(Exception e);
    }

    public void getDonHangById(String id, OnDataLoadedSingle callback) {
        db.collection("DonHang")
                .document(id)
                .get()
                .addOnSuccessListener(doc -> {
                    DonHang dh = doc.toObject(DonHang.class);
                    callback.onSuccess(dh);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getDonHangByNguoiDungId(String userId, OnDataLoaded callback) {

        db.collection("DonHang")
                .whereEqualTo("nguoiDungId", userId)
                .get()
                .addOnSuccessListener(query -> {

                    List<DonHang> list = new ArrayList<>();

                    for (DocumentSnapshot doc : query) {
                        DonHang dh = doc.toObject(DonHang.class);

                        if (dh != null) {
                            dh.setDonHangId(doc.getId());
                            list.add(dh);
                        }
                    }

                    callback.onSuccess(list);
                })
                .addOnFailureListener(callback::onError);
    }

    public void getMyOrders(OnDataLoaded callback) {

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            callback.onError(new Exception("Chưa đăng nhập"));
            return;
        }

        getDonHangByNguoiDungId(userId, callback);
    }
    public interface OnChiTietLoaded {
        void onSuccess(List<ChiTietDonHang> list);
        void onError(Exception e);
    }
    public void getChiTietDonHang(String donHangId,
                                  OnChiTietLoaded callback) {

        db.collection("ChiTietDonHang")
                .whereEqualTo("donHangId", donHangId)
                .get()
                .addOnSuccessListener(query -> {
                    List<ChiTietDonHang> list = new ArrayList<>();

                    for (DocumentSnapshot doc : query) {
                        ChiTietDonHang ct =
                                doc.toObject(ChiTietDonHang.class);

                        list.add(ct);
                    }

                    callback.onSuccess(list);
                });
    }

    public interface OnDiaChiMacDinhLoaded {
        void onSuccess(DiaChi diaChi);
        void onError(Exception e);
    }

    public void getDiaChiTheoNguoiDungId(String nguoiDungId,
                                                OnDiaChiMacDinhLoaded callback) {

        db.collection("DiaChi")
                .whereEqualTo("nguoiDungId", nguoiDungId)
                .whereEqualTo("macDinh", true)
                .limit(1)
                .get()
                .addOnSuccessListener(query -> {

                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);

                        DiaChi diaChi = doc.toObject(DiaChi.class);

                        if (diaChi != null) {
                            diaChi.setDiaChiId(doc.getId());
                            callback.onSuccess(diaChi);
                            return;
                        }
                    }

                    callback.onSuccess(null);
                })
                .addOnFailureListener(callback::onError);
    }
}
