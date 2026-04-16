package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.SanPham;
import com.example.hethongbangiay.repositories.SanPhamRepository;

import java.util.List;

public class AdminProductAdapter extends BaseAdapter {

    private Context context;
    private List<SanPham> listSP;
    SanPhamRepository repo;

    public AdminProductAdapter(Context context, List<SanPham> list) {
        this.context = context;
        this.listSP = list;
    }

    @Override
    public int getCount() {
        return listSP.size();
    }

    @Override
    public Object getItem(int position) {
        return listSP.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.admin_product_item, parent, false);
        }

        SanPham sp = listSP.get(position);

        ImageView img = convertView.findViewById(R.id.imgProduct);
        TextView name = convertView.findViewById(R.id.txtName);
        TextView price = convertView.findViewById(R.id.txtPrice);
        TextView rating = convertView.findViewById(R.id.txtRating);
        TextView sold = convertView.findViewById(R.id.txtSold);
        ImageView btnDelete = convertView.findViewById(R.id.btnDelete);

        repo = new SanPhamRepository();
        name.setText(sp.getTenSanPham());
        price.setText(String.format("%,.0f đ", sp.getDonGia()));
        rating.setText(String.format("%.1f", sp.getDiemDanhGia()));
        sold.setText(sp.getLuotBan() + " sold");

        Glide.with(context)
                .load(sp.getAnhSanPham())
                .placeholder(R.drawable.shoes)
                .error(R.drawable.shoes)
                .into(img);

        btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận ẩn sản phẩm")
                    .setMessage("Bạn có chắc muốn ẩn: " + sp.getTenSanPham() + " ?")
                    .setPositiveButton("Ẩn", (dialog, which) -> {

                        repo.anSanPham(
                                sp.getSanPhamId(),

                                unused -> {
                                    Toast.makeText(context, "Đã ẩn", Toast.LENGTH_SHORT).show();

                                    sp.setActive(false);
                                    notifyDataSetChanged();
                                },

                                e -> Toast.makeText(context, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );

                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        return convertView;
    }
    public void updateData(List<SanPham> newList) {
        this.listSP.clear();
        this.listSP.addAll(newList);
        notifyDataSetChanged();
    }
}
