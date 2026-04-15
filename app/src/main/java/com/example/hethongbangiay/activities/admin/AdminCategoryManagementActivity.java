package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.AdminDMAdapter;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.database.DanhMucDB;
import java.util.ArrayList;

public class AdminCategoryManagementActivity extends AppCompatActivity {
    public ListView listView;
    TextView txtEmpty;
    Button btnAdd;
    ArrayList<DanhMuc> danhMucs;
    AdminDMAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category_management);

        listView = findViewById(R.id.lvDM);
        txtEmpty = findViewById(R.id.txtEmpty);
        btnAdd = findViewById(R.id.btnDMAdd);

        btnAdd.setOnClickListener(v -> {
            DanhMucDB db = new DanhMucDB(this);
            String newId = db.generateNewId();
            openAddDialog(newId);
        });

        DanhMucDB danhMucDB = new DanhMucDB(this);
        if (danhMucDB.getAllDM().isEmpty()) {
            danhMucDB.themDMtest();
        }

        danhMucs = danhMucDB.getAllDM();

        adapter = new AdminDMAdapter(this,danhMucs);
        listView.setAdapter(adapter);
        listView.setEmptyView(txtEmpty);
        reloadData();

    }
    public void reloadData() {
        DanhMucDB db = new DanhMucDB(this);
        danhMucs.clear();
        danhMucs.addAll(db.getAllDM());

        adapter.notifyDataSetChanged();
    }
    public void openAddDialog(String newId) {
        DanhMuc dm = new DanhMuc();
        dm.setDanhMucId(newId);
        dm.setTenDanhMuc("");
        dm.setMoTaDanhMuc("");
        dm.setAnhDanhMuc("");
        dm.setActive(true);

        adapter.showEditDialog(dm);
    }
}