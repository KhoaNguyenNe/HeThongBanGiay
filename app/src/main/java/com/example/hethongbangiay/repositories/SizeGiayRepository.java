package com.example.hethongbangiay.repositories;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.models.SizeGiay;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SizeGiayRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void laySizeTheoSanPhamId(String sanPhamId, @NonNull OnFirestoreResult<List<SizeGiay>> listener) {
        db.collection("SanPham")
                .document(sanPhamId)
                .collection("Sizes")
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<SizeGiay> list = new ArrayList<>();
                    for (var doc : snapshot.getDocuments()) {
                        SizeGiay size = FirestoreMapper.toSizeGiay(doc, sanPhamId);
                        if (size.getSoLuong() > 0) {
                            list.add(size);
                        }
                    }
                    list.sort((a, b) -> Integer.compare(a.getSize(), b.getSize()));
                    listener.onSuccess(list);
                })
                .addOnFailureListener(listener::onError);
    }
}