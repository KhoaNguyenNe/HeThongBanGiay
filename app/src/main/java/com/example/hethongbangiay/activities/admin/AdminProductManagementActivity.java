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
import com.example.hethongbangiay.models.NguoiDung;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.repositories.UserRepository;
import com.example.hethongbangiay.utils.RoleUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminProductManagementActivity extends AppCompatActivity {
    ListView lvSP;
    Spinner spnDM;
    List<DanhMuc> listDM;
    List<SanPham> listSP;
    Button btnAdd;
    SanPhamDB dbsp;
    DanhMucDB dbdm;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
//    boolean isFirst;
    AdminProductAdapter adapter;
    private final UserRepository userRepository = new UserRepository();

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

        validatePermissionAndLoad();
    }

    private void validatePermissionAndLoad() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRepository.getUserProfile(uid)
                .addOnSuccessListener(documentSnapshot -> {
                    NguoiDung profile;
                    try {
                        profile = documentSnapshot.toObject(NguoiDung.class);
                    } catch (RuntimeException ex) {
                        Toast.makeText(this, "Lỗi dữ liệu hồ sơ: " + ex.getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                        return;
                    }

                    String role = RoleUtils.normalizeRole(profile != null ? profile.getVaiTro() : null);
                    if (!RoleUtils.canManageProducts(role)) {
                        Toast.makeText(this, "Bạn không có quyền quản lý Product", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    initProductManagement();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không xác thực được quyền: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    finish();
                });
    }

    private void initProductManagement() {
        dbsp = new SanPhamDB(this);
        dbdm = new DanhMucDB(this);

        if (dbdm.getAllDM().isEmpty()) {
            dbdm.themDMtest();
        }

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

                loadSanPhamTheoDanhMuc(dm.getDanhMucId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        btnAdd.setOnClickListener(v -> {

            if (listDM == null || listDM.isEmpty()) {
                Toast.makeText(this, "Chưa có danh mục", Toast.LENGTH_SHORT).show();
                return;
            }


            DanhMuc dm = listDM.get(spnDM.getSelectedItemPosition());

            ProductAddBottomSheet sheet =
                    ProductAddBottomSheet.newInstance(dm.getDanhMucId());

            sheet.show(getSupportFragmentManager(), "AddProduct");
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