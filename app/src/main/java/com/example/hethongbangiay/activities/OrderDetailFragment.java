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
import com.example.hethongbangiay.utils.OnFirestoreResult;
import com.example.hethongbangiay.viewmodels.OrderViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OrderDetailFragment extends Fragment {

    private RecyclerView rcv;
    private OrderDetailAdapter adapter;
    private List<ChiTietDonHang> list;

    private OrderViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TextView txtHeader = view.findViewById(R.id.txtHeaderTitle);
        txtHeader.setText("Thông tin đơn hàng");

        rcv = view.findViewById(R.id.recyclerViewOrderDetailItem);

        // init list + adapter
        list = new ArrayList<>();
        adapter = new OrderDetailAdapter(list, item ->{
            moDialogDanhGia(item);
        });


        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setAdapter(adapter);

        // lấy orderId
        String orderId = null;
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
            }
        });
        viewModel.getChiTietDonHang().observe(getViewLifecycleOwner(), ds -> {
            list.clear();
            list.addAll(ds);
            adapter.notifyDataSetChanged();
        });

    }
    private void moDialogDanhGia(ChiTietDonHang item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_review, null);
        TextView txtTenSanPham = view.findViewById(R.id.txtTenSanPhamDanhGia);
        txtTenSanPham.setText(item.getTenSanPham() + " - Đánh giá" + item.getSanPhamId());
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