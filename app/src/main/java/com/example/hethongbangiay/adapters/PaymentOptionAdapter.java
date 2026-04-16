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

import java.util.ArrayList;
import java.util.List;

public class PaymentOptionAdapter extends RecyclerView.Adapter<PaymentOptionAdapter.ViewHolder> {

    public interface OnPaymentSelectedListener {
        void onPaymentSelected(PaymentOption option);
    }

    public static class PaymentOption {
        private final String ma;
        private final String ten;
        private final String moTa;
        private final int iconResId;

        public PaymentOption(String ma, String ten, String moTa, @DrawableRes int iconResId) {
            this.ma = ma;
            this.ten = ten;
            this.moTa = moTa;
            this.iconResId = iconResId;
        }

        public String getMa() {
            return ma;
        }

        public String getTen() {
            return ten;
        }

        public String getMoTa() {
            return moTa;
        }

        public int getIconResId() {
            return iconResId;
        }
    }

    private final List<PaymentOption> data = new ArrayList<>();
    private final OnPaymentSelectedListener listener;
    private String maDangChon = "";

    public PaymentOptionAdapter(OnPaymentSelectedListener listener) {
        this.listener = listener;
    }

    public void capNhatDuLieu(List<PaymentOption> dsMoi, String maDangChon) {
        data.clear();
        if (dsMoi != null) {
            data.addAll(dsMoi);
        }
        this.maDangChon = maDangChon == null ? "" : maDangChon;
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
        PaymentOption option = data.get(position);

        holder.imgOptionIcon.setImageResource(option.getIconResId());
        holder.tvOptionTitle.setText(option.getTen());

        if (option.getMoTa() == null || option.getMoTa().trim().isEmpty()) {
            holder.tvOptionSubtitle.setVisibility(View.GONE);
        } else {
            holder.tvOptionSubtitle.setVisibility(View.VISIBLE);
            holder.tvOptionSubtitle.setText(option.getMoTa());
        }

        holder.tvOptionBadge.setVisibility(View.GONE);
        holder.tvOptionPrice.setVisibility(View.GONE);
        holder.btnOptionEdit.setVisibility(View.GONE);
        holder.btnOptionDelete.setVisibility(View.GONE);
        holder.rbOption.setChecked(option.getMa().equals(maDangChon));

        holder.itemView.setOnClickListener(v -> {
            maDangChon = option.getMa();
            notifyDataSetChanged();
            listener.onPaymentSelected(option);
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
