package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Glide là thư viện ngoài đã có sẵn trong project để tải ảnh.
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.ChiTietDonHang;
import com.example.hethongbangiay.utils.ImageResolver;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartActionListener {
        void onTangSoLuong(ChiTietDonHang item);

        void onGiamSoLuong(ChiTietDonHang item);

        void onXoaSanPham(ChiTietDonHang item);
    }

    private final List<ChiTietDonHang> data = new ArrayList<>();
    private final OnCartActionListener listener;
    private final NumberFormat tienTe = NumberFormat.getInstance(new Locale("vi", "VN"));

    public CartAdapter(OnCartActionListener listener) {
        this.listener = listener;
    }

    public void capNhatDuLieu(List<ChiTietDonHang> dsMoi) {
        data.clear();
        if (dsMoi != null) {
            data.addAll(dsMoi);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ChiTietDonHang item = data.get(position);

        holder.tvName.setText(item.getTenSanPham());
        holder.tvSize.setText("Size = " + item.getSizeGiay());
        holder.tvPrice.setText(tienTe.format(item.getGiaTien()) + " đ");
        holder.tvQty.setText(String.valueOf(item.getSoLuong()));

        if (item.getMauSac() == null || item.getMauSac().trim().isEmpty()) {
            holder.viewColorDot.setVisibility(View.GONE);
            holder.tvColor.setVisibility(View.GONE);
        } else {
            holder.viewColorDot.setVisibility(View.VISIBLE);
            holder.tvColor.setVisibility(View.VISIBLE);
            holder.tvColor.setText(item.getMauSac());
        }

        int fallback = ImageResolver.resolveFallbackDrawable(holder.itemView.getContext(), item.getAnhSanPham());
        String imageUrl = ImageResolver.resolveImage(item.getAnhSanPham());
        if (imageUrl == null) {
            holder.imgProduct.setImageResource(fallback);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(fallback)
                    .error(fallback)
                    .into(holder.imgProduct);
        }

        holder.btnMinus.setOnClickListener(v -> listener.onGiamSoLuong(item));
        holder.btnPlus.setOnClickListener(v -> listener.onTangSoLuong(item));
        holder.btnDelete.setOnClickListener(v -> listener.onXoaSanPham(item));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        ImageView btnDelete;
        View viewColorDot;
        TextView tvName;
        TextView tvColor;
        TextView tvSize;
        TextView tvPrice;
        TextView btnMinus;
        TextView tvQty;
        TextView btnPlus;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            viewColorDot = itemView.findViewById(R.id.viewColorDot);
            tvName = itemView.findViewById(R.id.tvName);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            tvQty = itemView.findViewById(R.id.tvQty);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
