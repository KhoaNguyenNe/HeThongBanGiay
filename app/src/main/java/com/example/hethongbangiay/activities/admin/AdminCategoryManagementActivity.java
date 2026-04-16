package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.AdminDMAdapter;
import com.example.hethongbangiay.models.DanhMuc;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminCategoryManagementActivity extends AppCompatActivity {
    public ListView listView;
    TextView txtEmpty;
    Button btnAdd;
    ArrayList<DanhMuc> danhMucs;
    AdminDMAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category_management);

        listView = findViewById(R.id.lvDM);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnAdd = findViewById(R.id.btnDMAdd);
        danhMucs = new ArrayList<>();
        adapter = new AdminDMAdapter(this, danhMucs);
        listView.setAdapter(adapter);
        listView.setEmptyView(txtEmpty);
        btnAdd.setOnClickListener(v -> {
            DanhMuc dm = new DanhMuc();

            CategoryBottomSheet.newInstance(dm)
                    .show(getSupportFragmentManager(), "Add");
        });
        reloadData();

    }
    public void reloadData() {
        loadDanhMuc();
    }
    public void openAddDialog(String newId) {
        DanhMuc dm = new DanhMuc();
        dm.setDanhMucId(newId);
        dm.setTenDanhMuc("");
        dm.setMoTaDanhMuc("");
        dm.setAnhDanhMuc("");
        dm.setActive(true);

//        adapter.showEditDialog(dm);
    }
    private void loadDanhMuc() {

        db.collection("DanhMuc")
                .get()
                .addOnSuccessListener(query -> {

                    if (danhMucs == null) {
                        danhMucs = new ArrayList<>();
                    }

                    danhMucs.clear();

                    for (DocumentSnapshot doc : query) {
                        DanhMuc dm = doc.toObject(DanhMuc.class);

                        if (dm != null) {
                            dm.setDanhMucId(doc.getId());
                            danhMucs.add(dm);
                        }
                    }

                    adapter.notifyDataSetChanged();

                    txtEmpty.setVisibility(
                            danhMucs.isEmpty() ? View.VISIBLE : View.GONE
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}