package com.example.hethongbangiay.repositories;

import android.util.Log;

import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.utils.PhuongThucThanhToan;
import com.example.hethongbangiay.utils.TrangThaiDonHang;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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

    public interface OnCreateOrderListener {
        void onSuccess(String orderId);

        void onError(Exception e);
    }

    public void createOrder(List<ChiTietDonHang> cart, OnCreateOrderListener listener) {
        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            listener.onError(new Exception("User chưa đăng nhập"));
            return;
        }
        if (cart == null || cart.isEmpty()) {
            listener.onError(new Exception("Giỏ hàng rỗng"));
            return;
        }
        double tongTien = 0;
        for (ChiTietDonHang item : cart) {
            tongTien += item.getGiaTien() * item.getSoLuong();
        }

        // Tạo đơn hàng
        DonHang donHang = new DonHang();
        donHang.setNguoiDungId(userId);
        donHang.setNgayDatHang(Timestamp.now());
        donHang.setTinhTrangDonHang(TrangThaiDonHang.CHO_XAC_NHAN);
        donHang.setPhuongThucThanhToan(PhuongThucThanhToan.COD);
        donHang.setChiTietSanPham(cart);
        donHang.setTongTien(tongTien);
        donHang.setNgayGiaoHang(null);
        donHang.setNgayHuy(null);
        Log.d("DEBUG_CART", cart.toString());

        // Lưu đơn hàng vào Firebase
        db.collection("DonHang")
                .add(donHang)
                .addOnSuccessListener(documentReference -> {

                    String id = documentReference.getId();

                    // set lại id
                    donHang.setDonHangId(id);

                    // update lại vào Firestore
                    db.collection("DonHang")
                            .document(id)
                            .update("donHangId", id);

                    listener.onSuccess(id);
                })
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
}
