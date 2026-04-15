package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.AdminProductAdapter;
import com.example.hethongbangiay.database.DanhMucDB;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.models.SanPham;

import java.util.ArrayList;
import java.util.List;

public class AdminProductManagementActivity extends AppCompatActivity {
    ListView lvSP;
    Spinner spnDM;
    List<DanhMuc> listDM;
    List<SanPham> listSP;
    SanPhamDB dbsp;
    DanhMucDB dbdm;
//    boolean isFirst;
    AdminProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_product_management);
        spnDM = findViewById(R.id.spnDM);
        lvSP = findViewById(R.id.lvSP);

        dbsp = new SanPhamDB(this);
        dbdm = new DanhMucDB(this);
        if (dbsp.layTatCaSpDangActive().isEmpty()) {
            dbsp.insertSampleSanPham();
        }
        loadDanhMuc();
//        isFirst = true;
        spnDM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (listDM == null || listDM.isEmpty()) return;

                DanhMuc dm = listDM.get(position);

                listSP = dbsp.getSanPhamByDanhMuc(dm.getDanhMucId());

                if (listSP == null) listSP = new ArrayList<>();

                adapter = new AdminProductAdapter(AdminProductManagementActivity.this, listSP);
                lvSP.setAdapter(adapter);

                Log.d("DM_ID", dm.getDanhMucId());
                Log.d("SP_SIZE", dbsp.getSanPhamByDanhMuc(dm.getDanhMucId()).size() + "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        lvSP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SanPham sp = listSP.get(position);

                ProductBottomSheet sheet = ProductBottomSheet.newInstance(sp.getSanPhamId());
                sheet.show(getSupportFragmentManager(), "ProductSheet");

            }
        });

    }
    private void loadDanhMuc() {

        listDM = dbdm.getAllDM();

        ArrayAdapter<DanhMuc> adapterDM =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        listDM);

        spnDM.setAdapter(adapterDM);

        if (!listDM.isEmpty()) {
            spnDM.setSelection(0);
        }
    }
}