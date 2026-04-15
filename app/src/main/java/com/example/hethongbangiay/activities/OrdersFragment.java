package com.example.hethongbangiay.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.OrderAdapter;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.repositories.DonHangRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.example.hethongbangiay.viewmodels.OrderViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView rcvOrder;
    private OrderAdapter adapter;
    private List<DonHang> list;
    private DonHangRepository repository;
    private Button btnCreateOrder, btnDatHang;
    private OrderViewModel orderViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        rcvOrder = view.findViewById(R.id.rcvOrder);

        list = new ArrayList<>();

        adapter = new OrderAdapter(list, donHang -> {
            Log.d("TEST", "Click detail order");
            OrderDetailFragment fragment = new OrderDetailFragment();

            Bundle bundle = new Bundle();
            bundle.putString("orderId", donHang.getDonHangId());
            fragment.setArguments(bundle);
            requireActivity().findViewById(R.id.scrollContent).setVisibility(View.GONE);

            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .addToBackStack(null)
                    .commit();
        });

        rcvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rcvOrder.setAdapter(adapter);
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);


        repository = new DonHangRepository();
        loadData();

        List<ChiTietDonHang> cartFake = new ArrayList<>();

        cartFake.add(new ChiTietDonHang(
                "ct07",
                "Giày Jordan 1 Low",
                500000,
                43,
                "Đỏ đen",
                1,
                "https://example.com/jordan.jpg",
                "sp07"
        ));

        cartFake.add(new ChiTietDonHang(
                "ct08",
                "Giày chạy bộ Asics",
                270000,
                42,
                "Xám",
                2,
                "https://example.com/asics.jpg",
                "sp08"
        ));

        btnCreateOrder = view.findViewById(R.id.btnCreateOrder);
        btnCreateOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderViewModel.taoDonHang(cartFake);
            }
        });

        btnDatHang = view.findViewById(R.id.btnThanhToan);
        btnDatHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireActivity(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void loadData() {
        repository.getMyOrders(new DonHangRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<DonHang> data) {
                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}