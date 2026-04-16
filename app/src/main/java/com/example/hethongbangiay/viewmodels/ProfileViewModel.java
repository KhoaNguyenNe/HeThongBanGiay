package com.example.hethongbangiay.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.repositories.AuthRepository;
import com.example.hethongbangiay.repositories.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class ProfileViewModel extends ViewModel {
    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<NguoiDung> profile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<NguoiDung> getProfile() {
        return profile;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadProfile() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            message.setValue("Chưa đăng nhập.");
            return;
        }

        loading.setValue(true);
        userRepository.getUserProfile(currentUser.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    loading.setValue(false);
                    if (documentSnapshot.exists()) {
                        try {
                            profile.setValue(documentSnapshot.toObject(NguoiDung.class));
                        } catch (RuntimeException ex) {
                            message.setValue("Lỗi đọc dữ liệu người dùng: " + ex.getMessage());
                        }
                    } else {
                        message.setValue("Không tìm thấy thông tin người dùng.");
                    }
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    public void logout() {
        authRepository.logout();
    }

    public void updatePhone(String phone) {
        updateField("soDienThoai", phone);
    }

    public void updateFullName(String fullName) {
        updateField("hoTen", fullName);
    }

    public void updateAvatar(String avatarUrl) {
        updateField("avatar", avatarUrl);
    }

    private void updateField(String field, Object value) {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser == null) {
            message.setValue("Chưa đăng nhập.");
            return;
        }

        loading.setValue(true);
        userRepository.updateUserField(currentUser.getUid(), field, value)
                .addOnSuccessListener(aVoid -> {
                    loading.setValue(false);
                    loadProfile();
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }
}