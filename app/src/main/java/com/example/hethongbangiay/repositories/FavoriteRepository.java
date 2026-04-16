package com.example.hethongbangiay.repositories;

import androidx.annotation.NonNull;

import com.example.hethongbangiay.firestore.FirestoreMapper;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavoriteRepository {

    private static final String COLLECTION_USERS = "NguoiDung";
    private static final String SUB_COLLECTION_FAVORITES = "YeuThich";
    private static final String COLLECTION_PRODUCTS = "SanPham";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public FavoriteRepository() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public void isCurrentUserFavorite(String sanPhamId, @NonNull OnFirestoreResult<Boolean> listener) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            listener.onSuccess(false);
            return;
        }

        getFavoritesCollection(currentUser.getUid())
                .document(sanPhamId)
                .get()
                .addOnSuccessListener(documentSnapshot -> listener.onSuccess(documentSnapshot.exists()))
                .addOnFailureListener(listener::onError);
    }

    public void toggleCurrentUserFavorite(SanPham sanPham, @NonNull OnFirestoreResult<Boolean> listener) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            listener.onError(new IllegalStateException("Bạn cần đăng nhập để dùng yêu thích"));
            return;
        }

        if (sanPham == null || sanPham.getSanPhamId() == null || sanPham.getSanPhamId().trim().isEmpty()) {
            listener.onError(new IllegalArgumentException("Sản phẩm không hợp lệ"));
            return;
        }

        CollectionReference favorites = getFavoritesCollection(currentUser.getUid());
        String sanPhamId = sanPham.getSanPhamId().trim();

        favorites.document(sanPhamId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        favorites.document(sanPhamId)
                                .delete()
                                .addOnSuccessListener(unused -> listener.onSuccess(false))
                                .addOnFailureListener(listener::onError);
                        return;
                    }

                    Map<String, Object> data = new HashMap<>();
                    data.put("sanPhamId", sanPhamId);
                    data.put("createdAt", System.currentTimeMillis());

                    favorites.document(sanPhamId)
                            .set(data)
                            .addOnSuccessListener(unused -> listener.onSuccess(true))
                            .addOnFailureListener(listener::onError);
                })
                .addOnFailureListener(listener::onError);
    }

    public void getCurrentUserFavoriteProductIds(@NonNull OnFirestoreResult<Set<String>> listener) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            listener.onSuccess(new HashSet<>());
            return;
        }

        getFavoritesCollection(currentUser.getUid())
                .get()
                .addOnSuccessListener(snapshot -> {
                    Set<String> ids = new HashSet<>();
                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        ids.add(document.getId());
                    }
                    listener.onSuccess(ids);
                })
                .addOnFailureListener(listener::onError);
    }

    public void getCurrentUserFavoriteProducts(@NonNull OnFirestoreResult<List<SanPham>> listener) {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            listener.onSuccess(new ArrayList<>());
            return;
        }

        getFavoritesCollection(currentUser.getUid())
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<String> favoriteIds = new ArrayList<>();
                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();

                    for (DocumentSnapshot document : snapshot.getDocuments()) {
                        favoriteIds.add(document.getId());
                        tasks.add(db.collection(COLLECTION_PRODUCTS).document(document.getId()).get());
                    }

                    if (tasks.isEmpty()) {
                        listener.onSuccess(new ArrayList<>());
                        return;
                    }

                    Tasks.whenAllSuccess(tasks)
                            .addOnSuccessListener(results -> {
                                Map<String, SanPham> productsById = new HashMap<>();
                                for (Object result : results) {
                                    if (!(result instanceof DocumentSnapshot)) {
                                        continue;
                                    }

                                    DocumentSnapshot productDoc = (DocumentSnapshot) result;
                                    if (!productDoc.exists()) {
                                        continue;
                                    }

                                    SanPham sanPham = FirestoreMapper.toSanPham(productDoc);
                                    if (sanPham != null && sanPham.isActive()) {
                                        productsById.put(sanPham.getSanPhamId(), sanPham);
                                    }
                                }

                                List<SanPham> data = new ArrayList<>();
                                for (String sanPhamId : favoriteIds) {
                                    SanPham sanPham = productsById.get(sanPhamId);
                                    if (sanPham != null) {
                                        data.add(sanPham);
                                    }
                                }

                                listener.onSuccess(data);
                            })
                            .addOnFailureListener(e -> listener.onError(asException(e)));
                })
                .addOnFailureListener(listener::onError);
    }

    private CollectionReference getFavoritesCollection(String uid) {
        return db.collection(COLLECTION_USERS)
                .document(uid)
                .collection(SUB_COLLECTION_FAVORITES);
    }

    private Exception asException(Exception exception) {
        return exception;
    }

    private Exception asException(Throwable throwable) {
        return throwable instanceof Exception ? (Exception) throwable : new Exception(throwable);
    }
}
