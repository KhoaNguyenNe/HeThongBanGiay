package com.example.hethongbangiay.activities;

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
import android.widget.TextView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.OrderDetailAdapter;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.viewmodels.OrderViewModel;

import java.util.ArrayList;
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

        // 🟢 init list + adapter
        list = new ArrayList<>();
        adapter = new OrderDetailAdapter(list);

        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setAdapter(adapter);

        // 🟢 lấy orderId
        String orderId = null;
        if (getArguments() != null) {
            orderId = getArguments().getString("orderId");
        }

        // 🟢 init ViewModel
        viewModel = new ViewModelProvider(this).get(OrderViewModel.class);

        // 🟢 gọi load data
        if (orderId != null) {
            viewModel.loadDonHangById(orderId);
        }

        // 🟢 observe data
        viewModel.getDonHangDetail().observe(getViewLifecycleOwner(), donHang -> {

            if (donHang != null && donHang.getChiTietSanPham() != null) {
                list.clear();
                list.addAll(donHang.getChiTietSanPham());
                adapter.notifyDataSetChanged();
            }
        });
    }
}