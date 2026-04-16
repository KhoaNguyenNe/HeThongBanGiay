package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class ShippingOptionAdapter extends RecyclerView.Adapter<ShippingOptionAdapter.ViewHolder> {

    public interface OnShippingSelectedListener {
        void onShippingSelected(ShippingOption option);
    }

    public static class ShippingOption {
        private final String ten;
        private final String moTa;
        private final int gia;
        private final int iconResId;

        public ShippingOption(String ten, String moTa, int gia, @DrawableRes int iconResId) {
            this.ten = ten;
            this.moTa = moTa;
            this.gia = gia;
            this.iconResId = iconResId;
        }

        public String getTen() {
            return ten;
        }

        public String getMoTa() {
            return moTa;
        }

        public int getGia() {
            return gia;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    private final List<ShippingOption> data = new ArrayList<>();
    private final OnShippingSelectedListener listener;
    private String tenDangChon = "";

    public ShippingOptionAdapter(OnShippingSelectedListener listener) {
        this.listener = listener;
    }

    public void capNhatDuLieu(List<ShippingOption> dsMoi, String tenDangChon) {
        data.clear();
        if (dsMoi != null) {
            data.addAll(dsMoi);
        }
        this.tenDangChon = tenDangChon == null ? "" : tenDangChon;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShippingOption option = data.get(position);

        holder.imgOptionIcon.setImageResource(option.getIconResId());
        holder.tvOptionTitle.setText(option.getTen());
        holder.tvOptionSubtitle.setText(option.getMoTa());
        holder.tvOptionPrice.setText(FormatUtils.formatCurrency(option.getGia()));
        holder.tvOptionPrice.setVisibility(View.VISIBLE);
        holder.tvOptionBadge.setVisibility(View.GONE);
        holder.btnOptionEdit.setVisibility(View.GONE);
        holder.btnOptionDelete.setVisibility(View.GONE);
        holder.rbOption.setChecked(option.getTen().equals(tenDangChon));

        holder.itemView.setOnClickListener(v -> {
            tenDangChon = option.getTen();
            notifyDataSetChanged();
            listener.onShippingSelected(option);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgOptionIcon;
        TextView tvOptionTitle;
        TextView tvOptionSubtitle;
        TextView tvOptionBadge;
        TextView tvOptionPrice;
        ImageView btnOptionEdit;
        ImageView btnOptionDelete;
        RadioButton rbOption;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgOptionIcon = itemView.findViewById(R.id.imgOptionIcon);
            tvOptionTitle = itemView.findViewById(R.id.tvOptionTitle);
            tvOptionSubtitle = itemView.findViewById(R.id.tvOptionSubtitle);
            tvOptionBadge = itemView.findViewById(R.id.tvOptionBadge);
            tvOptionPrice = itemView.findViewById(R.id.tvOptionPrice);
            btnOptionEdit = itemView.findViewById(R.id.btnOptionEdit);
            btnOptionDelete = itemView.findViewById(R.id.btnOptionDelete);
            rbOption = itemView.findViewById(R.id.rbOption);
        }
    }
}
