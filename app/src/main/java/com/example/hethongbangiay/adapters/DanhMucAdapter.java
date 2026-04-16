package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.DanhMuc;
import com.example.hethongbangiay.utils.ImageResolver;

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
        holder.cardCategoryIcon.setBackgroundResource(
                isSelected ? R.drawable.bg_circle_primary_outlined : R.drawable.bg_circle_surface_alt
        );
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
        ImageResolver.loadImageReference(imageView, imageReference, android.R.drawable.ic_menu_gallery);
    }

    public void capNhatDuLieu(java.util.List<com.example.hethongbangiay.models.DanhMuc> danhSachMoi) {
        this.danhSachDanhMuc.clear();
        this.danhSachDanhMuc.addAll(danhSachMoi);
        notifyDataSetChanged();
    }

    static class DanhMucViewHolder extends RecyclerView.ViewHolder {
        FrameLayout cardCategoryIcon;
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

