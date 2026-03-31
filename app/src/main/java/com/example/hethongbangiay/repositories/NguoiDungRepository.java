package com.example.hethongbangiay.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NguoiDungRepository {
    private final FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public NguoiDungRepository() {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // Đăng nhập bằng Email và Password
    public Task<AuthResult> login(String email, String password) {
        return mAuth.signInWithEmailAndPassword(email, password);
    }

    // Lấy thông tin User hiện tại từ Firebase Auth
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    // Lấy thông tin chi tiết từ Firestore (nếu cần dùng sau này)
    public Task<DocumentSnapshot> getUserProfile(String uid) {
        return db.collection("NguoiDung").document(uid).get();
    }

    // Đăng xuất
    public void logout() {
        mAuth.signOut();
    }
}