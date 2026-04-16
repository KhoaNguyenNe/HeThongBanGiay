package com.example.hethongbangiay.activities.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;
import com.example.hethongbangiay.models.DanhMuc;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;


public class CategoryBottomSheet extends BottomSheetDialogFragment {
    EditText edtTen, edtMoTa;
    ImageView imgPreview;
    Button btnPickImg, btnSave;
    DanhMuc dm;
    boolean isNew;
    Uri imageUri;
    String imageUrl = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static CategoryBottomSheet newInstance(DanhMuc dm) {
        CategoryBottomSheet sheet = new CategoryBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", dm);
        sheet.setArguments(bundle);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_add_category, container, false);

        edtTen = view.findViewById(R.id.edtTen);
        edtMoTa = view.findViewById(R.id.edtMoTa);
        imgPreview = view.findViewById(R.id.imgPreview);
        btnPickImg = view.findViewById(R.id.btnPickImg);
        btnSave = view.findViewById(R.id.btnSave);

        btnPickImg.setOnClickListener(v -> pickImage());

        btnSave.setOnClickListener(v -> saveDanhMuc());
        dm = (DanhMuc) getArguments().getSerializable("data");

        if (dm == null) {
            dm = new DanhMuc();
            isNew = true;
        } else {
            isNew = (dm.getTenDanhMuc() == null || dm.getTenDanhMuc().isEmpty());
        }
        if (!isNew) {
            edtTen.setText(dm.getTenDanhMuc());
            edtMoTa.setText(dm.getMoTaDanhMuc());

            if (dm.getAnhDanhMuc() != null && !dm.getAnhDanhMuc().isEmpty()) {
                Glide.with(getContext())
                        .load(dm.getAnhDanhMuc())
                        .into(imgPreview);
            }
        }
        return view;
    }
    ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imageUri = result.getData().getData();
                    imgPreview.setImageURI(imageUri);
                }
            });
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    private void uploadImageAndSave() {

        if (imageUri == null) {
            // giữ ảnh cũ
            if (dm.getAnhDanhMuc() == null) {
                dm.setAnhDanhMuc("");
            }

            saveToFirestore(dm);
            return;
        }

        MediaManager.get().upload(imageUri)
                .unsigned(CloudinaryConfig.UPLOAD_PRESET)
                .option("folder", CloudinaryConfig.FOLDER)
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String imageUrl = resultData.get("secure_url").toString();

                        dm.setAnhDanhMuc(imageUrl); // 👉 set trực tiếp vào object

                        saveToFirestore(dm);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(getContext(), "Upload lỗi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }
    private void saveDanhMuc() {
        String ten = edtTen.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();

        if (ten.isEmpty()) {
            Toast.makeText(getContext(), "Nhập tên!", Toast.LENGTH_SHORT).show();
            return;
        }

        dm.setTenDanhMuc(ten);
        dm.setMoTaDanhMuc(moTa);
        dm.setActive(true);

        if (dm.getDanhMucId() == null || dm.getDanhMucId().isEmpty()) {
            dm.setDanhMucId(db.collection("DanhMuc").document().getId());
        }

        uploadImageAndSave();
    }

    private void saveToFirestore(DanhMuc dm) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("DanhMuc")
                .document(dm.getDanhMucId())
                .set(dm)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(),
                            isNew ? "Thêm thành công" : "Cập nhật thành công",
                            Toast.LENGTH_SHORT).show();

                    reload();
                    dismiss();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi", Toast.LENGTH_SHORT).show()
                );
    }
    private void reload() {
        if (getActivity() instanceof AdminCategoryManagementActivity) {
            ((AdminCategoryManagementActivity) getActivity()).reloadData();
        }
    }
}

