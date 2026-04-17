package com.example.hethongbangiay.activities;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.OrderDetailAdapter;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DanhGia;
import com.example.hethongbangiay.repositories.DanhGiaRepository;
import com.example.hethongbangiay.utils.ImageResolver;
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.viewmodels.OrderViewModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDetailFragment extends Fragment {

    private RecyclerView rcv;
    private OrderDetailAdapter adapter;
    private List<ChiTietDonHang> list;

    private OrderViewModel viewModel;
    private String orderId;
    TextView txtNguoiNhan, txtDiaChi, txtSdt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView txtHeader = view.findViewById(R.id.txtHeaderTitle);
        txtHeader.setText("Thông tin đơn hàng");
        txtNguoiNhan = view.findViewById(R.id.txtreceiver);
        txtDiaChi = view.findViewById(R.id.txtAddressReceiver);
        txtSdt = view.findViewById(R.id.txtPhoneNumber);


        rcv = view.findViewById(R.id.recyclerViewOrderDetailItem);

        // init list + adapter
        list = new ArrayList<>();
        adapter = new OrderDetailAdapter(list, item ->{
            moDialogDanhGia(item);
        });


        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setAdapter(adapter);

        // lấy orderId
        orderId = null;
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }

        // init ViewModel
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);


        // gọi load data
        if (orderId != null) {
            viewModel.loadDonHangById(orderId);
        }

        // observe data
        viewModel.getDonHangDetail().observe(getViewLifecycleOwner(), donHang -> {

            if (donHang != null) {

                viewModel.loadChiTietDonHang(donHang.getDonHangId());
                viewModel.loadDiaChiGiaoHang(donHang.getNguoiDungId());
            }
        });
        viewModel.getChiTietDonHang().observe(getViewLifecycleOwner(), ds -> {
            list.clear();
            list.addAll(ds);
            capNhatTrangThaiDanhGia(ds);
            adapter.notifyDataSetChanged();
        });

        viewModel.getDiaChiGiaoHang().observe(getViewLifecycleOwner(), diaChi -> {

            if (diaChi != null) {

                txtNguoiNhan.setText("Người nhận: "+diaChi.getTenNguoiNhan());

                txtSdt.setText("Số điện thoại: "+diaChi.getSoDienThoai());

                txtDiaChi.setText("Địa chỉ: " + diaChi.getDiaChi());

            } else {

                txtNguoiNhan.setText("Chưa có địa chỉ");
                txtSdt.setText("");
                txtDiaChi.setText("");

            }

        });

    }

    private void capNhatTrangThaiDanhGia(List<ChiTietDonHang> ds) {
        if (ds == null || ds.isEmpty()) {
            return;
        }

        String uid = FirebaseAuth.getInstance().getUid();
        if (uid == null || uid.trim().isEmpty()) {
            return;
        }

        // Đảm bảo mỗi sản phẩm trong đơn chỉ được đánh giá 1 lần (theo docId cố định)
        DanhGiaRepository repo = new DanhGiaRepository();
        Map<String, ChiTietDonHang> byKey = new HashMap<>();
        for (ChiTietDonHang item : ds) {
            if (item == null) continue;
            String dhId = item.getDonHangId() == null ? orderId : item.getDonHangId();
            item.setDonHangId(dhId);
            String spId = item.getSanPhamId();
            if (dhId == null || spId == null) continue;
            String key = DanhGiaRepository.buildDanhGiaId(dhId, spId, uid);
            byKey.put(key, item);
        }

        List<Task<?>> tasks = new ArrayList<>();
        for (String key : byKey.keySet()) {
            com.google.android.gms.tasks.Task<com.google.firebase.firestore.DocumentSnapshot> task = FirebaseFirestore.getInstance()
                    .collection("DanhGia")
                    .document(key)
                    .get();
            task.addOnSuccessListener(snapshot -> {
                ChiTietDonHang item = byKey.get(key);
                if (item != null) {
                    item.setDaDanhGia(snapshot != null && snapshot.exists());
                }
            });
            tasks.add(task);
        }

        Tasks.whenAllComplete(tasks)
                .addOnSuccessListener(unused -> adapter.notifyDataSetChanged());
    }

    private void moDialogDanhGia(ChiTietDonHang item) {
        if (item == null) {
            return;
        }

        if (item.isDaDanhGia()) {
            Toast.makeText(requireContext(), "Sản phẩm này đã được đánh giá trong đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_review, null);
        ImageView imgSanPhamDanhGia = view.findViewById(R.id.imgSanPhamDanhGia);
        TextView txtTenSanPham = view.findViewById(R.id.txtTenSanPhamDanhGia);
        txtTenSanPham.setText("Đánh giá");
        if (imgSanPhamDanhGia != null) {
            ImageResolver.loadImageReference(imgSanPhamDanhGia, item.getAnhSanPham());
        }
        ImageView star1 = view.findViewById(R.id.star1);
        ImageView star2 = view.findViewById(R.id.star2);
        ImageView star3 = view.findViewById(R.id.star3);
        ImageView star4 = view.findViewById(R.id.star4);
        ImageView star5 = view.findViewById(R.id.star5);
        Button btnGui = view.findViewById(R.id.btnSubmitReview);
        EditText edtReview = view.findViewById(R.id.edtReview);
        capNhatSao(view, 0);

        final int[] rating = {0};

        star1.setOnClickListener(v -> {
            rating[0] = 1;
            capNhatSao(view, 1);
        });

        star2.setOnClickListener(v -> {
            rating[0] = 2;
            capNhatSao(view, 2);
        });

        star3.setOnClickListener(v -> {
            rating[0] = 3;
            capNhatSao(view, 3);
        });

        star4.setOnClickListener(v -> {
            rating[0] = 4;
            capNhatSao(view, 4);
        });

        star5.setOnClickListener(v -> {
            rating[0] = 5;
            capNhatSao(view, 5);
        });
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        btnGui.setOnClickListener(v -> {
            String comment =edtReview.getText()
                            .toString()
                            .trim();

            if (rating[0] == 0) {
                Toast.makeText(requireContext(),"Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            DanhGia dg = new DanhGia();

            dg.setNguoiDungId(uid);
            dg.setSanPhamId(item.getSanPhamId());
            dg.setDonHangId(item.getDonHangId() == null ? orderId : item.getDonHangId());
            dg.setRating(rating[0]);
            dg.setComment(comment);
            dg.setNgayDanhGia(new Date());
            DanhGiaRepository repo = new DanhGiaRepository();
            repo.themDanhGia(dg, new OnFirestoreResult<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            Toast.makeText(
                                    requireContext(),
                                    "Đánh giá thành công",
                                    Toast.LENGTH_SHORT
                            ).show();
                            item.setDaDanhGia(true);
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                        @Override
                        public void onError(Exception e) {

                            Toast.makeText(
                                    requireContext(),
                                    "Lỗi: " + e.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
            );
        });
    }
    private void capNhatSao(View view, int soSao) {

        ImageView[] stars = {
                view.findViewById(R.id.star1),
                view.findViewById(R.id.star2),
                view.findViewById(R.id.star3),
                view.findViewById(R.id.star4),
                view.findViewById(R.id.star5)
        };

        for (int i = 0; i < 5; i++) {
            if (i < soSao) {
                stars[i].setImageResource(R.drawable.ic_star_full);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_border);
            }
        }
    }

}