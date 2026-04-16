package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
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

    private TextInputEditText edtHoTen, edtSoDienThoai, edtEmail;
    private ImageView imgAvatar;
    private Button btnSave, btnCancel;
    private TextView txtHeader, txtChangeAvatar;
    private View headerSearchContainer;
    private View headerEdtSearch;

    private ProfileViewModel profileViewModel;
    private NguoiDung currentProfile;
    private ActivityResultLauncher<String> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_edit_profile);
        ThemeUtils.applySystemBars(this);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        initViews();
        initActions();
        applyInsets();
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
        edtEmail = findViewById(R.id.edtEmail);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        txtHeader = findViewById(R.id.txtHeaderTitle);
        txtChangeAvatar = findViewById(R.id.txtChangeAvatar);
        headerSearchContainer = findViewById(R.id.headerSearchContainer);
        headerEdtSearch = findViewById(R.id.headerEdtSearch);

        txtHeader.setText("Chỉnh sửa hồ sơ");
    }

    private void initActions() {
        btnSave.setOnClickListener(v -> saveProfile());
        btnCancel.setOnClickListener(v -> finish());

        View.OnClickListener moChonAnh = v -> imagePickerLauncher.launch("image/*");
        imgAvatar.setOnClickListener(moChonAnh);
        txtChangeAvatar.setOnClickListener(moChonAnh);

        setupHeaderSearch();
    }

    private void setupHeaderSearch() {
        if (headerSearchContainer == null) return;

        headerSearchContainer.setVisibility(View.VISIBLE);
        View.OnClickListener openSearch = v -> startActivity(new Intent(this, SearchActivity.class));
        headerSearchContainer.setOnClickListener(openSearch);
        if (headerEdtSearch != null) {
            headerEdtSearch.setOnClickListener(openSearch);
        }
    }

    private void observeViewModel() {
        profileViewModel.getProfile().observe(this, this::bindProfile);
        profileViewModel.getMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        });
        profileViewModel.getLoading().observe(this, isLoading -> {
            boolean enabled = !Boolean.TRUE.equals(isLoading);
            btnSave.setEnabled(enabled);
            btnCancel.setEnabled(enabled);
            txtChangeAvatar.setEnabled(enabled);
            imgAvatar.setEnabled(enabled);
        });
    }

    private void bindProfile(NguoiDung nguoiDung) {
        if (nguoiDung == null) return;
        currentProfile = nguoiDung;

        edtHoTen.setText(nguoiDung.getHoTen());
        edtSoDienThoai.setText(nguoiDung.getSoDienThoai());
        edtEmail.setText(nguoiDung.getEmail());

        ImageResolver.loadAvatar(imgAvatar, nguoiDung.getAvatar());
    }

    private void saveProfile() {
        String hoTen = edtHoTen.getText() != null ? edtHoTen.getText().toString().trim() : "";
        String soDienThoai = edtSoDienThoai.getText() != null ? edtSoDienThoai.getText().toString().trim() : "";

        if (hoTen.isEmpty()) {
            edtHoTen.setError("Nhập họ tên");
            return;
        }

        boolean hasChanged = false;
        if (currentProfile == null || !hoTen.equals(valueOrEmpty(currentProfile.getHoTen()))) {
            profileViewModel.updateFullName(hoTen);
            hasChanged = true;
        }
        if (currentProfile == null || !soDienThoai.equals(valueOrEmpty(currentProfile.getSoDienThoai()))) {
            profileViewModel.updatePhone(soDienThoai);
            hasChanged = true;
        }

        if (!hasChanged) {
            Toast.makeText(this, "Không có thay đổi để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private String valueOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private void applyInsets() {
        View root = findViewById(R.id.editProfileRoot);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }
}
