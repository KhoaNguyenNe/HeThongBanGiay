package com.example.hethongbangiay.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.activities.admin.AdminCategoryManagementActivity;
import com.example.hethongbangiay.activities.admin.CategoryBottomSheet;
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
        ImageView img = convertView.findViewById(R.id.imgDM);
        TextView txtName = convertView.findViewById(R.id.txtCategoryName);
        TextView txtMoTa = convertView.findViewById(R.id.txtDMMoTa);
        Button btnEdit = convertView.findViewById(R.id.btnCategoryChange);
        DanhMuc dm = list.get(position);
        txtName.setText(dm.getTenDanhMuc());
        txtMoTa.setText(dm.getMoTaDanhMuc());
        if (dm.getAnhDanhMuc() != null && !dm.getAnhDanhMuc().isEmpty()) {
            Glide.with(context)
                    .load(dm.getAnhDanhMuc())
                    .placeholder(R.drawable.shoes)
                    .into(img);
        } else {
            img.setImageResource(R.drawable.shoes);
        }

        btnEdit.setOnClickListener(v -> {
            CategoryBottomSheet.newInstance(dm)
                    .show(((AppCompatActivity) context).getSupportFragmentManager(), "Edit");
        });

        return convertView;
    }
}
