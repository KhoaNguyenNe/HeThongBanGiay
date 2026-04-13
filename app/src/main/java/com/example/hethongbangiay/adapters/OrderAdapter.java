package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.models.DonHang;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<DonHang> list;
    public OrderAdapter(List<DonHang> list){
        this.list = list;
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

            // sản phẩm đầu tiên
            ChiTietDonHang sp = listSP.get(0);

            holder.txtName.setText(sp.getTenSanPham());
            holder.txtColor.setText(sp.getMauSac() + " - Size: " + sp.getSizeGiay());

            // tổng tiền
            int total = 0;
            for (ChiTietDonHang item : listSP) {
                total += item.getGiaTien(); // nếu có số lượng thì * soLuong
            }

            holder.txtPrice.setText(String.format("Tổng: %,d đ", total));

            // số sản phẩm
            if (listSP.size() > 1) {
                holder.txtExtra.setText("+" + (listSP.size() - 1) + " sản phẩm khác");
                holder.txtExtra.setVisibility(View.VISIBLE);
            } else {
                holder.txtExtra.setVisibility(View.GONE);
            }

            // trạng thái
            holder.txtStatus.setText(donHang.getTinhTrangDonHang());

        } else {
            holder.txtName.setText("Không có sản phẩm");
            holder.txtColor.setText("");
            holder.txtPrice.setText("");
            holder.txtExtra.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder{
        TextView txtName, txtColor, txtSize, txtPrice, txtExtra, txtStatus;
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtProductName);
            txtColor = itemView.findViewById(R.id.txtProductColor);
            txtSize = itemView.findViewById(R.id.txtProductSize);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtExtra = itemView.findViewById(R.id.txtExtra);
            txtStatus = itemView.findViewById(R.id.txtStatus);
        }
    }
}
