package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.hethongbangiay.R;
import com.example.hethongbangiay.cloudinary.CloudinaryConfig;
import com.example.hethongbangiay.models.DanhMuc;

import java.util.List;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.DanhMucViewHolder> {

    private final Context context;
    private final List<DanhMuc> danhSachDanhMuc;

    public DanhMucAdapter(Context context, List<DanhMuc> danhSachDanhMuc) {
        this.context = context;
        this.danhSachDanhMuc = danhSachDanhMuc;
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

    static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCategory;
        TextView tvCategoryName;

        public DanhMucViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCategory = itemView.findViewById(R.id.imgCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
