package com.example.hethongbangiay.repositories;

import com.example.hethongbangiay.models.NguoiDung;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.example.hethongbangiay.utils.RoleUtils;

import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private final FirebaseFirestore db;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> saveUserProfile(NguoiDung user) {
        normalizeUserBeforeSave(user, false);
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
        normalizeUserBeforeSave(user, true);
        return db.collection("NguoiDung")
                .document(user.getUid())
                .set(user);
    }

    public Task<Void> updateUserField(String uid, String field, Object value) {
        Map<String, Object> update = new HashMap<>();
        update.put(field, value);
        update.put("updatedAt", System.currentTimeMillis());
        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    public Task<QuerySnapshot> getAllUsers() {
        return db.collection("NguoiDung").get();
    }

    public Task<Void> updateUserRole(String uid, String role) {
        return updateUserField(uid, "vaiTro", RoleUtils.normalizeRole(role));
    }

    public Task<Void> lockUser(String uid, boolean locked) {
        return setUserLockState(uid, locked);
    }

    public Task<Void> createUserByAdmin(NguoiDung user) {
        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            taskCompletionSource.setException(new IllegalArgumentException("Email không hợp lệ"));
            return taskCompletionSource.getTask();
        }

        String email = user.getEmail().trim().toLowerCase();
        String uid = user.getUid();
        if (uid == null || uid.trim().isEmpty()) {
            uid = db.collection("NguoiDung").document().getId();
            user.setUid(uid);
        }
        final String targetUid = uid;

        normalizeUserBeforeSave(user, false);
        user.setEmail(email);

        db.collection("NguoiDung")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (!snapshot.isEmpty()) {
                        taskCompletionSource.setException(new IllegalStateException("Email đã tồn tại"));
                        return;
                    }

                    db.collection("NguoiDung")
                            .document(targetUid)
                            .set(user)
                            .addOnSuccessListener(unused -> taskCompletionSource.setResult(null))
                            .addOnFailureListener(taskCompletionSource::setException);
                })
                .addOnFailureListener(taskCompletionSource::setException);

        return taskCompletionSource.getTask();
    }

    public Task<Void> updateUserBasicInfo(String uid, String hoTen, String email, String soDienThoai, String avatar) {
        Map<String, Object> update = new HashMap<>();
        update.put("hoTen", hoTen != null ? hoTen.trim() : "");
        update.put("email", email != null ? email.trim().toLowerCase() : "");
        update.put("soDienThoai", soDienThoai != null ? soDienThoai.trim() : "");
        update.put("avatar", avatar != null ? avatar.trim() : "");
        update.put("updatedAt", System.currentTimeMillis());

        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    public Task<Void> setUserLockState(String uid, boolean locked) {
        Map<String, Object> update = new HashMap<>();
        update.put("locked", locked);
        update.put("active", !locked);
        update.put("updatedAt", System.currentTimeMillis());

        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    public Task<Void> setUserActiveState(String uid, boolean active) {
        Map<String, Object> update = new HashMap<>();
        update.put("active", active);
        update.put("locked", !active);
        update.put("updatedAt", System.currentTimeMillis());

        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    public Task<Void> softDeleteUser(String uid) {
        Map<String, Object> update = new HashMap<>();
        update.put("deleted", true);
        update.put("active", false);
        update.put("locked", true);
        update.put("updatedAt", System.currentTimeMillis());

        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    public Task<Void> updateLastLogin(String uid) {
        Map<String, Object> update = new HashMap<>();
        long now = System.currentTimeMillis();
        update.put("lastLoginAt", now);
        update.put("updatedAt", now);

        return db.collection("NguoiDung")
                .document(uid)
                .update(update);
    }

    private void normalizeUserBeforeSave(NguoiDung user, boolean keepCreatedAt) {
        if (user == null) {
            return;
        }

        long now = System.currentTimeMillis();

        user.setEmail(user.getEmail() != null ? user.getEmail().trim().toLowerCase() : "");
        user.setHoTen(user.getHoTen() != null ? user.getHoTen().trim() : "");
        user.setSoDienThoai(user.getSoDienThoai() != null ? user.getSoDienThoai().trim() : "");
        user.setAvatar(user.getAvatar() != null ? user.getAvatar().trim() : "");
        user.setVaiTro(RoleUtils.normalizeRole(user.getVaiTro()));

        if (user.getLocked() == null) {
            user.setLocked(false);
        }
        if (user.getActive() == null) {
            user.setActive(true);
        }
        if (user.getDeleted() == null) {
            user.setDeleted(false);
        }

        if (!keepCreatedAt || user.getCreatedAt() == null) {
            user.setCreatedAt(now);
        }
        user.setUpdatedAt(now);
    }
}