package com.example.hethongbangiay.activities.admin;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryManager;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.repositories.SanPhamRepository;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProductAddBottomSheet extends BottomSheetDialogFragment {

    EditText edtName, edtPrice, edtDesc;
    Button btnAddPic, btnSave;
    ImageView imgPreview;

    Uri imageUri;
    String danhMucId;

    SanPhamRepository repo = new SanPhamRepository();

    // 📲 chọn ảnh
    ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {

                            imageUri = result.getData().getData();

                            Glide.with(this)
                                    .load(imageUri)
                                    .into(imgPreview);
                        }
                    }
            );

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_add_product, container, false);

        edtName = view.findViewById(R.id.edtName);
        edtPrice = view.findViewById(R.id.edtPrice);
        edtDesc = view.findViewById(R.id.edtDesc);
        btnAddPic = view.findViewById(R.id.btnAddPic);
        btnSave = view.findViewById(R.id.btnSave);
        imgPreview = view.findViewById(R.id.imgPreview);

        if (getArguments() != null) {
            danhMucId = getArguments().getString("danhMucId");
        }


        CloudinaryManager.init(requireContext());

        btnAddPic.setOnClickListener(v -> openGallery());
        imgPreview.setOnClickListener(v -> openGallery());

        btnSave.setOnClickListener(v -> uploadImageAndSave());

        return view;
    }


    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }
    public static ProductAddBottomSheet newInstance(String danhMucId) {
        ProductAddBottomSheet sheet = new ProductAddBottomSheet();

        Bundle args = new Bundle();
        args.putString("danhMucId", danhMucId);

        sheet.setArguments(args);

        return sheet;
    }

    private void uploadImageAndSave() {

        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();

        if (name.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(getContext(), "Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }


        btnSave.setEnabled(false);

        if (imageUri == null) {
            saveProduct(""); // không có ảnh
            return;
        }

        MediaManager.get().upload(imageUri)
                .unsigned(CloudinaryConfig.UPLOAD_PRESET)
                .option("folder", CloudinaryConfig.FOLDER)
                .callback(new UploadCallback() {

                    @Override
                    public void onStart(String requestId) {
                        Toast.makeText(getContext(), "Đang upload ảnh...", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {

                        String imageUrl = resultData.get("secure_url").toString();

                        saveProduct(imageUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        btnSave.setEnabled(true);
                        Toast.makeText(getContext(), "Upload lỗi", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                })
                .dispatch();
    }


    private void saveProduct(String imageUrl) {

        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String desc = edtDesc.getText().toString().trim();

        double price = Double.parseDouble(priceStr);
        String id = UUID.randomUUID().toString();

        SanPham sp = new SanPham(
                id,
                danhMucId,
                name,
                price,
                imageUrl,
                desc,
                true
        );

        repo.addSanPham(sp, task -> {
            btnSave.setEnabled(true);

            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Thêm thành công", Toast.LENGTH_SHORT).show();
                dismiss();
            } else {
                Toast.makeText(getContext(), "Lỗi khi lưu Firestore", Toast.LENGTH_SHORT).show();
            }
        });
    }
}