package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.SanPham;

import java.util.List;

public class AdminProductAdapter extends BaseAdapter {

    private Context context;
    private List<SanPham> listSP;

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

        name.setText(sp.getTenSanPham());
        price.setText(String.format("%,.0f đ", sp.getDonGia()));
        rating.setText(String.format("%.1f", sp.getDiemDanhGia()));
        sold.setText(sp.getLuotBan() + " sold");

        Glide.with(context)
                .load(sp.getAnhSanPham())
                .placeholder(R.drawable.shoes)
                .error(R.drawable.shoes)
                .into(img);

        return convertView;
    }
}
