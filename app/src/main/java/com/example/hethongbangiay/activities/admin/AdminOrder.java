package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.AdminOrderAdapter;
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.repositories.DonHangRepository;

import java.util.ArrayList;
import java.util.List;

public class AdminOrder extends AppCompatActivity {
    ListView lvOrder;
    TextView txtEmpty;

    List<DonHang> list = new ArrayList<>();
    AdminOrderAdapter adapter;

    DonHangRepository repo = new DonHangRepository();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_order);
        lvOrder = findViewById(R.id.lvOrder);
        txtEmpty = findViewById(R.id.txtEmpty);

        adapter = new AdminOrderAdapter(this, list);
        lvOrder.setAdapter(adapter);

        loadDonHang();
    }

    private void loadDonHang() {

        repo.getAllDonHang(new DonHangRepository.OnDataLoaded() {
            @Override
            public void onSuccess(List<DonHang> data) {

                list.clear();
                list.addAll(data);
                adapter.notifyDataSetChanged();

                if (list.isEmpty()) {
                    txtEmpty.setVisibility(View.VISIBLE);
                    lvOrder.setVisibility(View.GONE);
                } else {
                    txtEmpty.setVisibility(View.GONE);
                    lvOrder.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(AdminOrder.this,
                        "Lỗi load: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}