package com.example.hethongbangiay.repositories;

import com.example.hethongbangiay.models.NguoiDung;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> saveUserProfile(NguoiDung user) {
        return db.collection("NguoiDung")
                .document(user.getUid())
                .set(user);
    }

    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return db.collection("NguoiDung")
                .document(uid)
                .get();
    }

    public Task<Void> updateUserProfile(NguoiDung user) {
        return db.collection("NguoiDung")
                .document(user.getUid())
                .set(user);
    }

    public Task<Void> updateUserField(String uid, String field, Object value) {
        Map<String, Object> update = new HashMap<>();
        update.put(field, value);
        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    public Task<QuerySnapshot> getAllUsers() {
        return db.collection("NguoiDung").get();
    }

    public Task<Void> updateUserRole(String uid, String role) {
        return updateUserField(uid, "vaiTro", role);
    }

    public Task<Void> lockUser(String uid, boolean locked) {
        return updateUserField(uid, "isLocked", locked);
    }
}