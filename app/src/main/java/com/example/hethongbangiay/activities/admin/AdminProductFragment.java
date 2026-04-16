package com.example.hethongbangiay.activities.admin;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.adapters.SizeAdapter;
import com.example.hethongbangiay.database.SizeGiayDB;
import com.example.hethongbangiay.models.SizeGiay;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AdminProductFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AdminProductFragment extends Fragment {

    private static final String ARG_ID = "sanPhamId";

    String sanPhamId;

    public static AdminProductFragment newInstance(String id) {
        AdminProductFragment fragment = new AdminProductFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ID, id);
        fragment.setArguments(bundle);
        return fragment;
    }

    public AdminProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sanPhamId = getArguments().getString(ARG_ID);
        }
    }
    EditText edtSize, edtQty;
    Button btnAddSize;

    List<SizeGiay> listSize;
    SizeAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.bottom_sheet_add_product, container, false);

        edtSize = v.findViewById(R.id.edtSize);
        edtQty = v.findViewById(R.id.edtQty);
        btnAddSize = v.findViewById(R.id.btnAddPic);
        ListView lvSize = v.findViewById(R.id.lvSize);

        SizeGiayDB db = new SizeGiayDB(requireContext());

        listSize = db.getBySanPhamId(sanPhamId);
        if (listSize == null) listSize = new ArrayList<>();

        adapter = new SizeAdapter(requireContext(), listSize);
        lvSize.setAdapter(adapter);

        btnAddSize.setOnClickListener(v1 -> addSize());

        return v;
    }
    private void addSize() {

        String sizeStr = edtSize.getText().toString().trim();
        String qtyStr = edtQty.getText().toString().trim();

        if (sizeStr.isEmpty() || qtyStr.isEmpty()) {
            Toast.makeText(getContext(), "Nhập size + số lượng", Toast.LENGTH_SHORT).show();
            return;
        }

        int size, qty;

        try {
            size = Integer.parseInt(sizeStr);
            qty = Integer.parseInt(qtyStr);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Sai định dạng số", Toast.LENGTH_SHORT).show();
            return;
        }

        SizeGiayDB db = new SizeGiayDB(requireContext());

        for (SizeGiay s : listSize) {
            if (s.getSize() == size) {
                s.setSoLuong(s.getSoLuong() + qty);
                adapter.notifyDataSetChanged();
                return;
            }
        }

        SizeGiay s = new SizeGiay();
        s.setSizeGiayId(db.generateSizeGiayId());
        s.setSanPhamId(sanPhamId);
        s.setSize(size);
        s.setSoLuong(qty);

        db.insertSizeGiay(s);

        listSize.add(s);
        adapter.notifyDataSetChanged();

        edtSize.setText("");
        edtQty.setText("");
    }
}