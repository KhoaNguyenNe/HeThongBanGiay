package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DonHang;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<DonHang> list;
    private OnItemClick listener;

    public interface OnItemClick {
        void onClick(DonHang donHang);
    }

    public OrderAdapter(List<DonHang> list, OnItemClick listener){
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        DonHang donHang = list.get(position);

        List<ChiTietDonHang> listSP = donHang.getChiTietSanPham();

        if (listSP != null && !listSP.isEmpty()) {

            ChiTietDonHang sp = listSP.get(0);

            holder.txtName.setText(sp.getTenSanPham());
            holder.txtColor.setText(sp.getMauSac() + " - Size: " + sp.getSizeGiay());

            int total = 0;
            for (ChiTietDonHang item : listSP) {
                total += item.getGiaTien();
            }

            holder.txtPrice.setText(String.format("Tổng: %,d đ", total));

            holder.txtStatus.setText(donHang.getTinhTrangDonHang());
        }

        holder.btnOrderDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(donHang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtColor, txtPrice, txtStatus;
        Button btnOrderDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCategoryName);
            txtColor = itemView.findViewById(R.id.txtProductColor);
            txtPrice = itemView.findViewById(R.id.txtDMMoTa);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            btnOrderDetail = itemView.findViewById(R.id.btnCategoryChange);
        }
    }
}
