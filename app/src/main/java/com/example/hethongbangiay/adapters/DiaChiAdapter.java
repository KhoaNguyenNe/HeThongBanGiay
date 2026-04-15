package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.DiaChi;

import java.util.ArrayList;
import java.util.List;

public class DiaChiAdapter extends RecyclerView.Adapter<DiaChiAdapter.DiaChiViewHolder> {

    public interface OnDiaChiSelectedListener {
        void onDiaChiSelected(DiaChi diaChi);

        void onSuaDiaChi(DiaChi diaChi);

        void onXoaDiaChi(DiaChi diaChi);
    }

    private final List<DiaChi> data = new ArrayList<>();
    private final OnDiaChiSelectedListener listener;
    private String diaChiDangChonId = "";

    public DiaChiAdapter(OnDiaChiSelectedListener listener) {
        this.listener = listener;
    }

    public void capNhatDuLieu(List<DiaChi> dsDiaChi, String diaChiDangChonId) {
        data.clear();
        if (dsDiaChi != null) {
            data.addAll(dsDiaChi);
        }
        this.diaChiDangChonId = diaChiDangChonId == null ? "" : diaChiDangChonId;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DiaChiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_address_home, parent, false);
        return new DiaChiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaChiViewHolder holder, int position) {
        DiaChi diaChi = data.get(position);
        holder.tvAddressTitle.setText(diaChi.getTenNguoiNhan());
        holder.tvAddressLine.setText(diaChi.getDiaChi() + " | " + diaChi.getSoDienThoai());
        holder.tvDefaultBadge.setVisibility(diaChi.isMacDinh() ? View.VISIBLE : View.GONE);
        holder.rbAddress.setChecked(diaChi.getDiaChiId().equals(diaChiDangChonId));
        holder.rbAddress.setClickable(false);
        holder.rbAddress.setFocusable(false);

        holder.itemView.setOnClickListener(v -> {
            diaChiDangChonId = diaChi.getDiaChiId();
            notifyDataSetChanged();
            listener.onDiaChiSelected(diaChi);
        });

        holder.btnEditAddressItem.setOnClickListener(v -> listener.onSuaDiaChi(diaChi));
        holder.btnDeleteAddressItem.setOnClickListener(v -> listener.onXoaDiaChi(diaChi));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class DiaChiViewHolder extends RecyclerView.ViewHolder {
        TextView tvAddressTitle;
        TextView tvDefaultBadge;
        TextView tvAddressLine;
        RadioButton rbAddress;
        ImageView btnEditAddressItem;
        ImageView btnDeleteAddressItem;

        public DiaChiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAddressTitle = itemView.findViewById(R.id.tvAddressTitle);
            tvDefaultBadge = itemView.findViewById(R.id.tvDefaultBadge);
            tvAddressLine = itemView.findViewById(R.id.tvAddressLine);
            rbAddress = itemView.findViewById(R.id.rbAddress);
            btnEditAddressItem = itemView.findViewById(R.id.btnEditAddressItem);
            btnDeleteAddressItem = itemView.findViewById(R.id.btnDeleteAddressItem);
        }
    }
}
