package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.AdminProductAdapter;
import com.example.hethongbangiay.database.DanhMucDB;
import com.example.hethongbangiay.database.SanPhamDB;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.models.SanPham;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminProductManagementActivity extends AppCompatActivity {
    ListView lvSP;
    Spinner spnDM;
    List<DanhMuc> listDM;
    List<SanPham> listSP;
    SanPhamDB dbsp;
    DanhMucDB dbdm;
    Button btnAdd;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    boolean isFirst;
    AdminProductAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_product_management);
        spnDM = findViewById(R.id.spnDM);
        lvSP = findViewById(R.id.lvSP);
        btnAdd = findViewById(R.id.btnAddSP);
//        dbsp = new SanPhamDB(this);
//        dbdm = new DanhMucDB(this);
//        if (dbsp.layTatCaSpDangActive().isEmpty()) {
//            dbsp.insertSampleSanPham();
//        }
        loadDanhMuc();
//        isFirst = true;


        spnDM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (listDM == null || listDM.isEmpty()) return;

                DanhMuc dm = listDM.get(position);

                loadSanPhamTheoDanhMuc(dm.getDanhMucId());
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
//    private void loadDanhMuc() {
//
//        listDM = dbdm.getAllDM();
//
//        ArrayAdapter<DanhMuc> adapterDM =
//                new ArrayAdapter<>(this,
//                        android.R.layout.simple_spinner_dropdown_item,
//                        listDM);
//
//        spnDM.setAdapter(adapterDM);
//
//        if (!listDM.isEmpty()) {
//            spnDM.setSelection(0);
//        }
//    }
    private void loadDanhMuc() {

        db.collection("DanhMuc")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    listDM = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        DanhMuc dm = doc.toObject(DanhMuc.class);
                        if (dm != null) {
                            dm.setDanhMucId(doc.getId());
                            listDM.add(dm);
                        }
                    }

                    ArrayAdapter<DanhMuc> adapterDM =
                            new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    listDM);

                    spnDM.setAdapter(adapterDM);

                    if (!listDM.isEmpty()) {
                        spnDM.setSelection(0);
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    private void loadSanPhamTheoDanhMuc(String danhMucId) {

        db.collection("SanPham")
                .whereEqualTo("danhMucId", danhMucId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    listSP = new ArrayList<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        SanPham sp = doc.toObject(SanPham.class);
                        if (sp != null) {
                            sp.setSanPhamId(doc.getId());
                            listSP.add(sp);
                        }
                    }

                    adapter = new AdminProductAdapter(this, listSP);
                    lvSP.setAdapter(adapter);

                    Log.d("SP_SIZE", String.valueOf(listSP.size()));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}