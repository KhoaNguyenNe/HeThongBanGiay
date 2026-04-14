package com.example.hethongbangiay.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.OrderDetailAdapter;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DonHang;

import java.util.ArrayList;
import java.util.List;
public class OrderDetailFragment extends Fragment {

    private RecyclerView rcv;
    private OrderDetailAdapter adapter;
    private List<ChiTietDonHang> list;

    private DonHang donHang;

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

        if (getArguments() != null) {
            donHang = (DonHang) getArguments().getSerializable("donHang");
        }

        list = new ArrayList<>();

        if (donHang != null && donHang.getChiTietSanPham() != null) {
            list.addAll(donHang.getChiTietSanPham());
        }
        Log.d("DEBUG_LIST", list.toString());


        adapter = new OrderDetailAdapter(list);

        rcv.setLayoutManager(new LinearLayoutManager(getContext()));
        rcv.setAdapter(adapter);
    }
}