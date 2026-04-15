package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;
import com.example.hethongbangiay.models.DanhMuc;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.DanhMucViewHolder> {

    public interface OnDanhMucClickListener {
        void onDanhMucClick(DanhMuc danhMuc);
    }

    private final Context context;
    private final List<DanhMuc> danhSachDanhMuc;
    private final OnDanhMucClickListener listener;
    private String selectedDanhMucId;

    public DanhMucAdapter(Context context,
                          List<DanhMuc> danhSachDanhMuc,
                          OnDanhMucClickListener listener) {
        this.context = context;
        this.danhSachDanhMuc = danhSachDanhMuc;
        this.listener = listener;
    }

    public void setSelectedDanhMucId(String selectedDanhMucId) {
        this.selectedDanhMucId = selectedDanhMucId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DanhMucViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new DanhMucViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DanhMucViewHolder holder, int position) {
        DanhMuc dm = danhSachDanhMuc.get(position);

        holder.tvCategoryName.setText(dm.getTenDanhMuc());
        bindCategoryImage(holder.imgCategory, dm.getAnhDanhMuc());

        boolean isSelected = dm.getDanhMucId() != null && dm.getDanhMucId().equals(selectedDanhMucId);
        holder.cardCategoryIcon.setCardBackgroundColor(ContextCompat.getColor(
                context,
                isSelected ? R.color.app_primary : R.color.app_surface_alt
        ));
        holder.cardCategoryIcon.setStrokeWidth(isSelected ? 2 : 0);
        holder.cardCategoryIcon.setStrokeColor(ContextCompat.getColor(context, R.color.app_text_primary));
        holder.tvCategoryName.setAlpha(isSelected ? 1f : 0.85f);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDanhMucClick(dm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return danhSachDanhMuc.size();
    }

    private void bindCategoryImage(ImageView imageView, String imageReference) {
        int fallback = android.R.drawable.ic_menu_gallery;

        if (imageReference == null || imageReference.trim().isEmpty()) {
            imageView.setImageResource(fallback);
            return;
        }

        String trimmed = imageReference.trim();

        int drawableResId = context.getResources()
                .getIdentifier(trimmed, "drawable", context.getPackageName());

        if (drawableResId != 0) {
            imageView.setImageResource(drawableResId);
            return;
        }

        String imageUrl;
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            imageUrl = trimmed;
        } else {
            imageUrl = "https://res.cloudinary.com/"
                    + CloudinaryConfig.CLOUD_NAME
                    + "/image/upload/"
                    + Uri.encode(trimmed, "/");
        }

        Glide.with(context)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(fallback)
                .error(fallback)
                .into(imageView);
    }

    public void capNhatDuLieu(java.util.List<com.example.hethongbangiay.models.DanhMuc> danhSachMoi) {
        this.danhSachDanhMuc.clear();
        this.danhSachDanhMuc.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardCategoryIcon;
        ImageView imgCategory;
        TextView tvCategoryName;

        public DanhMucViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategoryIcon = itemView.findViewById(R.id.cardCategoryIcon);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}

