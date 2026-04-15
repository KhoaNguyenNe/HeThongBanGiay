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

public class CheckoutAdapter extends RecyclerView.Adapter<CheckoutAdapter.CheckoutViewHolder> {

    private final List<ChiTietDonHang> data = new ArrayList<>();
    private final NumberFormat tienTe = NumberFormat.getInstance(new Locale("vi", "VN"));

    public void capNhatDuLieu(List<ChiTietDonHang> dsMoi) {
        data.clear();
        if (dsMoi != null) {
            data.addAll(dsMoi);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_product, parent, false);
        return new CheckoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        ChiTietDonHang item = data.get(position);

        holder.tvName.setText(item.getTenSanPham());
        holder.tvMeta.setText("Size = " + item.getSizeGiay());
        holder.tvPrice.setText(tienTe.format(item.getGiaTien()) + " đ");
        holder.tvQty.setText(String.valueOf(item.getSoLuong()));

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
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvName;
        TextView tvMeta;
        TextView tvPrice;
        TextView tvQty;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvMeta = itemView.findViewById(R.id.tvMeta);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
        }
    }
}
