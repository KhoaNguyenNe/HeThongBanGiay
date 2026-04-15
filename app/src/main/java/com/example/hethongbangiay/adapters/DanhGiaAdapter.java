package com.example.hethongbangiay.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;
import com.example.hethongbangiay.models.DanhGia;

import java.util.ArrayList;
import java.util.List;

public class DanhGiaAdapter extends RecyclerView.Adapter<DanhGiaAdapter.ViewHolder> {
    private final List<DanhGia> data = new ArrayList<>();

    public void submitData(List<DanhGia> newData) {
        data.clear();
        if (newData != null) {
            data.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DanhGia danhGia = data.get(position);

        holder.tvReviewUser.setText("User: " + danhGia.getNguoiDungId());
        holder.tvReviewRating.setText("★ " + danhGia.getRating());
        holder.tvReviewComment.setText(danhGia.getComment() == null ? "" : danhGia.getComment());
        holder.tvReviewLikeCount.setText("0");
        holder.tvReviewTime.setText("Moi day");
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewUser, tvReviewRating, tvReviewComment, tvReviewLikeCount, tvReviewTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewUser = itemView.findViewById(R.id.tvReviewUser);
            tvReviewRating = itemView.findViewById(R.id.tvReviewRating);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewLikeCount = itemView.findViewById(R.id.tvReviewLikeCount);
            tvReviewTime = itemView.findViewById(R.id.tvReviewTime);
        }
    }
}
