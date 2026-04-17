package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.utils.ImageResolver;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private List<ChiTietDonHang> list;

    public OrderDetailAdapter(List<ChiTietDonHang> list) {
        this.list = list;
    }
    public interface OnReviewClickListener {
        void onReviewClick(ChiTietDonHang item);
    }

    private OnReviewClickListener listener;

    public OrderDetailAdapter(List<ChiTietDonHang> list, OnReviewClickListener listener){
        this.list = list;
        this.listener = listener;
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
        holder.txtSize.setText("Cỡ: " + sp.getSizeGiay());
//        holder.txtColor.setText("Màu sắc: " + sp.getMauSac());
        holder.txtSoluong.setText("Số lượng: " + sp.getSoLuong());
        ImageResolver.loadImageReference(holder.imgProduct, sp.getAnhSanPham());
        if (sp.isDaDanhGia()) {
            holder.btnDanhGia.setEnabled(false);
            holder.btnDanhGia.setText("Đã đánh giá");
            holder.btnDanhGia.setOnClickListener(null);
        } else {
            holder.btnDanhGia.setEnabled(true);
            holder.btnDanhGia.setText("Đánh giá");
            holder.btnDanhGia.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReviewClick(sp);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtPrice, txtSize, txtColor, txtSoluong, txtNguoiNhan, txtDiaChi, txtSdt;
        Button btnDanhGia;
        ImageView imgProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCategoryName);
            txtPrice = itemView.findViewById(R.id.txtDMMoTa);
            txtSize = itemView.findViewById(R.id.txtProductSize);
            txtColor = itemView.findViewById(R.id.txtProductColor);
            txtSoluong = itemView.findViewById(R.id.txtSoluong);
            btnDanhGia = itemView.findViewById(R.id.btnDanhGia);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtNguoiNhan = itemView.findViewById(R.id.txtreceiver);
            txtDiaChi = itemView.findViewById(R.id.txtAddressReceiver);
            txtSdt = itemView.findViewById(R.id.txtPhoneNumber);
        }
    }
}
