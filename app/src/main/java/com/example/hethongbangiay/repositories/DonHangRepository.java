package com.example.hethongbangiay.repositories;

import com.example.hethongbangiay.models.DonHang;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

}
