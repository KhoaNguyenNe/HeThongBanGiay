package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.ChiTietDonHang;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<ChiTietDonHang> list;

    public OrderDetailAdapter(List<ChiTietDonHang> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ChiTietDonHang sp = list.get(position);

        holder.txtName.setText(sp.getTenSanPham());
        holder.txtPrice.setText("Giá: " + sp.getGiaTien() + "đ");
        holder.txtSize.setText("Size: " + sp.getSizeGiay());
        holder.txtColor.setText("Màu sắc: " + sp.getMauSac());
        holder.txtSoluong.setText("Số lượng: " + sp.getSoLuong());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtPrice, txtSize, txtColor, txtSoluong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtSize = itemView.findViewById(R.id.txtProductSize);
            txtColor = itemView.findViewById(R.id.txtProductColor);
            txtSoluong = itemView.findViewById(R.id.txtSoluong);
        }
    }
}
