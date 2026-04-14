package com.example.hethongbangiay.activities;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.OrderAdapter;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.repositories.DonHangRepository;
import com.example.hethongbangiay.viewmodels.OrderViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView rcvOrder;
    private OrderAdapter adapter;
    private List<DonHang> list;
    private DonHangRepository repository;

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
            bundle.putSerializable("donHang", donHang);
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

        repository = new DonHangRepository();
        loadData();

        return view;
    }

    private void loadData() {
        repository.getAllDonHang(new DonHangRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<DonHang> data) {
                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Lỗi load dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}