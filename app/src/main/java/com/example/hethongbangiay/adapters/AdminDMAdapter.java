package com.example.hethongbangiay.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.admin.AdminCategoryManagementActivity;
import com.example.hethongbangiay.database.DanhMucDB;
import com.example.hethongbangiay.models.DanhMuc;

import java.util.ArrayList;

public class AdminDMAdapter extends BaseAdapter {
    Context context;
    ArrayList<DanhMuc> list;
//    public AdminDMAdapter(Context context, ArrayList<DanhMuc> list) {
//        this.context = context;
//        this.list = list;
//    }
    public AdminDMAdapter(Context context, ArrayList<DanhMuc> list) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
    }
//    @Override
//    public int getCount() {
//        return list.size();
//    }
    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }
//    @Override
//    public Object getItem(int i) {
//        return list.get(i);
//    }
    @Override
    public Object getItem(int i) {
        return (list == null) ? null : list.get(i);
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.admin_category_item, parent, false);
        }

        TextView txtName = convertView.findViewById(R.id.txtCategoryName);
        TextView txtMoTa = convertView.findViewById(R.id.txtDMMoTa);
        Button btnEdit = convertView.findViewById(R.id.btnCategoryChange);

        DanhMuc dm = list.get(position);

        txtName.setText(dm.getTenDanhMuc());
        txtMoTa.setText(dm.getMoTaDanhMuc());

        btnEdit.setOnClickListener(v -> showEditDialog(dm));

        return convertView;
    }
    public void showEditDialog(DanhMuc dm) {
        boolean isNew = (dm.getTenDanhMuc() == null || dm.getTenDanhMuc().isEmpty());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.admin_edit_dm_dialog, null);

        EditText edtTen = view.findViewById(R.id.edtTen);
        EditText edtMoTa = view.findViewById(R.id.edtMoTa);
        EditText edtAnh = view.findViewById(R.id.edtAnh);

        // set data cũ nếu là sửa
        if (!isNew) {
            edtTen.setText(dm.getTenDanhMuc());
            edtMoTa.setText(dm.getMoTaDanhMuc());
            edtAnh.setText(dm.getAnhDanhMuc());
        }

        builder.setView(view);
        builder.setTitle(isNew ? "Thêm danh mục" : "Sửa danh mục");

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ten = edtTen.getText().toString().trim();
            String moTa = edtMoTa.getText().toString().trim();
            String anh = edtAnh.getText().toString().trim();

            if (ten.isEmpty()) {
                Toast.makeText(context, "Tên không được trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            dm.setTenDanhMuc(ten);
            dm.setMoTaDanhMuc(moTa);
            dm.setAnhDanhMuc(anh);
            dm.setActive(true);

            DanhMucDB db = new DanhMucDB(context);

            if (isNew) {
                db.insertDanhMuc(dm);
                Toast.makeText(context, "Đã thêm!", Toast.LENGTH_SHORT).show();
            } else {
                db.updateDanhMuc(dm);
                Toast.makeText(context, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
            }

            reload();
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }
    private void reload() {
        if (context instanceof AdminCategoryManagementActivity) {
            ((AdminCategoryManagementActivity) context).reloadData();
        }
    }
}
