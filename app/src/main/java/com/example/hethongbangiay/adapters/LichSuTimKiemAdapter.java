package com.example.hethongbangiay.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hethongbangiay.R;

import java.util.ArrayList;
import java.util.List;

public class LichSuTimKiemAdapter extends RecyclerView.Adapter<LichSuTimKiemAdapter.LSTKViewHolder> {

    public interface OnRecentActionListener {
        void onRecentClick(String keyword);
        void onDeleteClick(String keyword);
    }

    private final Context context;
    private final List<String> LSTKKeywords = new ArrayList<>();
    private final OnRecentActionListener listener;

    public LichSuTimKiemAdapter(Context context, List<String> data, OnRecentActionListener listener) {
        this.context = context;
        if (data != null) {
            LSTKKeywords.addAll(data);
        }
        this.listener = listener;
    }

    public void capNhatDuLieu(List<String> data) {
        LSTKKeywords.clear();
        if (data != null) {
            LSTKKeywords.addAll(data);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LSTKViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_search, parent, false);
        return new LSTKViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LSTKViewHolder holder, int position) {
        String keyword = LSTKKeywords.get(position);
        holder.tvKeyword.setText(keyword);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecentClick(keyword);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(keyword);
            }
        });
    }

    @Override
    public int getItemCount() {
        return LSTKKeywords.size();
    }

    static class LSTKViewHolder extends RecyclerView.ViewHolder {
        TextView tvKeyword;
        ImageView ivDelete;

        public LSTKViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKeyword = itemView.findViewById(R.id.tvRecentKeyword);
            ivDelete = itemView.findViewById(R.id.ivDeleteRecent);
        }
    }
}
