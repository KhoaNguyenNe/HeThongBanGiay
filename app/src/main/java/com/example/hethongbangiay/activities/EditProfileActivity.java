package com.example.hethongbangiay.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;
import com.example.hethongbangiay.cloudinary.ImageManager;
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.utils.ImageResolver;
import com.example.hethongbangiay.utils.ThemeUtils;
import com.example.hethongbangiay.viewmodels.ProfileViewModel;
import com.google.android.material.textfield.TextInputEditText;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText edtHoTen, edtSoDienThoai;
    private ImageView imgAvatar;
    private Button btnSave, btnUploadAvatar;

    private ProfileViewModel profileViewModel;
    private NguoiDung currentProfile;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ThemeUtils.applySystemBars(this);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        initActions();
        observeViewModel();
        profileViewModel.loadProfile();

        // Khởi tạo image picker
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelected
        );
    }

    private void initViews() {
        edtHoTen = findViewById(R.id.edtHoTen);
        edtSoDienThoai = findViewById(R.id.edtSoDienThoai);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnSave = findViewById(R.id.btnSave);
        btnUploadAvatar = findViewById(R.id.btnUploadAvatar);
    }

    private void initActions() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnUploadAvatar.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
    }

    private void observeViewModel() {
        profileViewModel.getProfile().observe(this, this::bindProfile);
        profileViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
        profileViewModel.getLoading().observe(this, isLoading -> {
            btnSave.setEnabled(!Boolean.TRUE.equals(isLoading));
        });
    }

    private void bindProfile(NguoiDung nguoiDung) {
        if (nguoiDung == null) return;
        currentProfile = nguoiDung;

        edtHoTen.setText(nguoiDung.getHoTen());
        edtSoDienThoai.setText(nguoiDung.getSoDienThoai());

        ImageResolver.loadAvatar(imgAvatar, nguoiDung.getAvatar());
    }

    private void saveProfile() {
        String hoTen = edtHoTen.getText() != null ? edtHoTen.getText().toString().trim() : "";
        String soDienThoai = edtSoDienThoai.getText() != null ? edtSoDienThoai.getText().toString().trim() : "";

        if (hoTen.isEmpty()) {
            edtHoTen.setError("Nhập họ tên");
            return;
        }

        profileViewModel.updateFullName(hoTen);
        profileViewModel.updatePhone(soDienThoai);

        Toast.makeText(this, "Đã lưu thay đổi", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void handleImageSelected(Uri uri) {
        if (uri != null) {
            ImageManager.uploadImage(uri, CloudinaryConfig.UPLOAD_PRESET, new ImageManager.ImageUploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    profileViewModel.updateAvatar(imageUrl);
                    ImageResolver.loadAvatar(imgAvatar, imageUrl);
                    Toast.makeText(EditProfileActivity.this, "Upload thành công", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(EditProfileActivity.this, "Lỗi upload: " + error, Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
