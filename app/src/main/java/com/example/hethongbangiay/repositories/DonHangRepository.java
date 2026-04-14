package com.example.hethongbangiay.repositories;

import android.util.Log;

import com.example.hethongbangiay.models.DonHang;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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



}
