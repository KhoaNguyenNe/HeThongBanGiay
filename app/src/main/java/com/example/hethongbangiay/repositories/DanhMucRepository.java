package com.example.hethongbangiay.repositories;



import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.models.DanhMuc;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DanhMucRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void layTatCaDMActive(@NonNull OnFirestoreResult<List<DanhMuc>> listener) {
        db.collection("DanhMuc")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<DanhMuc> list = new ArrayList<>();
                    for (var doc : snapshot.getDocuments()) {
                        list.add(FirestoreMapper.toDanhMuc(doc));
                    }
                    list.sort((a, b) -> a.getTenDanhMuc().compareToIgnoreCase(b.getTenDanhMuc()));
                    listener.onSuccess(list);
                })
                .addOnFailureListener(listener::onError);
    }
    public void anDanhMuc(String id,
                          OnSuccessListener<Void> success,
                          OnFailureListener failure) {

        db.collection("DanhMuc")
                .document(id)
                .update("active", false)
                .addOnSuccessListener(success)
                .addOnFailureListener(failure);
    }
}