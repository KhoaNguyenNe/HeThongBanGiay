package com.example.hethongbangiay.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.utils.Constants;
import com.example.hethongbangiay.repositories.AuthRepository;
import com.example.hethongbangiay.repositories.UserRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;

public class AuthViewModel extends ViewModel {
    private final AuthRepository authRepository = new AuthRepository();
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final MutableLiveData<FirebaseUser> authUser = new MutableLiveData<>();
    private final MutableLiveData<NguoiDung> userProfile = new MutableLiveData<>();

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public LiveData<FirebaseUser> getAuthUser() {
        return authUser;
    }

    public LiveData<NguoiDung> getUserProfile() {
        return userProfile;
    }

    public void login(String email, String password) {
        loading.setValue(true);
        authRepository.login(email, password)
                .addOnSuccessListener(authResult -> {
                    loading.setValue(false);
                    FirebaseUser user = authResult.getUser();
                    authUser.setValue(user);
                    if (user != null) {
                        loadUserProfile(user.getUid());
                    } else {
                        message.setValue("Đăng nhập thành công nhưng không lấy được user.");
                    }
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    public void register(String fullName, String email, String password) {
        loading.setValue(true);
        authRepository.register(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = authResult.getUser();
                    if (firebaseUser == null) {
                        loading.setValue(false);
                        message.setValue("Đăng ký thất bại: không lấy được user.");
                        return;
                    }

                    NguoiDung nguoiDung = new NguoiDung(
                            firebaseUser.getUid(),
                            email,
                            fullName,
                            "",
                            "",
                            Constants.VAI_TRO_USER
                    );

                    userRepository.saveUserProfile(nguoiDung)
                            .addOnSuccessListener(aVoid -> {
                                loading.setValue(false);
                                authUser.setValue(firebaseUser);
                                userProfile.setValue(nguoiDung);
                            })
                            .addOnFailureListener(e -> {
                                loading.setValue(false);
                                message.setValue("Lưu profile thất bại: " + e.getLocalizedMessage());
                            });
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    public void loginWithGoogle(String idToken) {
        loading.setValue(true);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        FirebaseAuth.getInstance()
                .signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    authUser.setValue(user);
                    if (user != null) {
                        loadOrCreateGoogleUserProfile(user);
                    } else {
                        loading.setValue(false);
                        message.setValue("Đăng nhập Google thất bại.");
                    }
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    private void loadOrCreateGoogleUserProfile(FirebaseUser user) {
        userRepository.getUserProfile(user.getUid())
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        NguoiDung profile = documentSnapshot.toObject(NguoiDung.class);
                        userProfile.setValue(profile);
                        loading.setValue(false);
                    } else {
                        NguoiDung newUser = new NguoiDung(
                                user.getUid(),
                                user.getEmail(),
                                user.getDisplayName() != null ? user.getDisplayName() : "",
                                "",
                                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "",
                                Constants.VAI_TRO_USER
                        );

                        userRepository.saveUserProfile(newUser)
                                .addOnSuccessListener(aVoid -> {
                                    userProfile.setValue(newUser);
                                    loading.setValue(false);
                                })
                                .addOnFailureListener(e -> {
                                    loading.setValue(false);
                                    message.setValue("Lưu profile Google thất bại: " + e.getLocalizedMessage());
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    public void sendPasswordReset(String email) {
        loading.setValue(true);
        authRepository.sendPasswordReset(email)
                .addOnSuccessListener(aVoid -> {
                    loading.setValue(false);
                    message.setValue("Đã gửi email reset mật khẩu. Vui lòng kiểm tra hộp thư.");
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    public void logout() {
        authRepository.logout();
        authUser.setValue(null);
        userProfile.setValue(null);
    }

    public void loadUserProfile(String uid) {
        loading.setValue(true);
        userRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    loading.setValue(false);
                    if (documentSnapshot.exists()) {
                        NguoiDung profile = documentSnapshot.toObject(NguoiDung.class);
                        userProfile.setValue(profile);
                    } else {
                        message.setValue("Không tìm thấy profile người dùng.");
                    }
                })
                .addOnFailureListener(e -> {
                    loading.setValue(false);
                    message.setValue(e.getLocalizedMessage());
                });
    }

    public void loadCurrentUserProfile() {
        FirebaseUser currentUser = authRepository.getCurrentUser();
        if (currentUser != null) {
            authUser.setValue(currentUser);
            loadUserProfile(currentUser.getUid());
        }
    }
}