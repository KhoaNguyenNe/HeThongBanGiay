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
import com.example.hethongbangiay.models.DonHang;
import com.example.hethongbangiay.repositories.DonHangRepository;
import com.example.hethongbangiay.utils.ImageResolver;

import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<DonHang> list;
    private OnItemClick listener;
    private DonHangRepository repository = new DonHangRepository();

    public interface OnItemClick {
        void onClick(DonHang donHang);
    }

    public OrderAdapter(List<DonHang> list, OnItemClick listener) {
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

        holder.txtTotal.setText(
                String.format(Locale.getDefault(),
                        "Tổng: %, .0f đ",
                        donHang.getTongTien())
        );

        holder.txtStatus.setText(donHang.getTinhTrangDonHang());

        holder.btnOrderDetail.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(donHang);
            }
        });

        // Load sản phẩm đầu tiên theo donHangId
        repository.getChiTietDonHang(donHang.getDonHangId(),
                new DonHangRepository.OnChiTietLoaded() {
                    @Override
                    public void onSuccess(List<ChiTietDonHang> ds) {

                        if (ds != null && !ds.isEmpty()) {

                            ChiTietDonHang sp = ds.get(0);

                            holder.txtName.setText(sp.getTenSanPham());
                            holder.txtColor.setText(sp.getMauSac());
                            holder.txtSize.setText(
                                    "Size: " + sp.getSizeGiay()
                            );

                            ImageResolver.loadImageReference(holder.imgOrderProduct, sp.getAnhSanPham());

                        } else {
                            holder.txtName.setText("Không có sản phẩm");
                            holder.txtColor.setText("");
                            holder.txtSize.setText("");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.txtName.setText("Lỗi tải sản phẩm");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtName, txtColor, txtTotal, txtStatus, txtSize;
        ImageView imgOrderProduct;
        Button btnOrderDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtCategoryName);
            txtColor = itemView.findViewById(R.id.txtProductColor);
            txtTotal = itemView.findViewById(R.id.txtTotal);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtSize = itemView.findViewById(R.id.txtProductSize);
            imgOrderProduct = itemView.findViewById(R.id.imgOrderProduct);
            btnOrderDetail = itemView.findViewById(R.id.btnOrderDetail);
        }
    }
}