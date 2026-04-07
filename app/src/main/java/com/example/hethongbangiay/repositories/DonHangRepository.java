package com.example.hethongbangiay.repositories;

import com.example.hethongbangiay.models.DonHang;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DonHangRepository {
    private final FirebaseFirestore db;

    public DonHangRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public void getAllOrders(OnSuccessListener<QuerySnapshot> onSuccess,
                             OnFailureListener onFailure) {
        db.collection("donhang")
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }


}
